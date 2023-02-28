package com.broll.gainea.client.network.sites;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.GameUtils;
import com.broll.gainea.client.ui.components.FinishedGoalDisplay;
import com.broll.gainea.client.ui.components.Popup;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.map.MapScrollUtils;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.ingame.windows.GoalOverlay;
import com.broll.gainea.client.ui.ingame.windows.LogWindow;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.MessageUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Event;
import com.broll.gainea.net.NT_Event_BoardEffect;
import com.broll.gainea.net.NT_Event_FinishedGoal;
import com.broll.gainea.net.NT_Event_FocusLocation;
import com.broll.gainea.net.NT_Event_FocusObject;
import com.broll.gainea.net.NT_Event_FocusObjects;
import com.broll.gainea.net.NT_Event_MovedObject;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedCard;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedGoal;
import com.broll.gainea.net.NT_Event_PlacedObject;
import com.broll.gainea.net.NT_Event_PlayedCard;
import com.broll.gainea.net.NT_Event_ReceivedCard;
import com.broll.gainea.net.NT_Event_ReceivedGoal;
import com.broll.gainea.net.NT_Event_ReceivedPoints;
import com.broll.gainea.net.NT_Event_ReceivedStars;
import com.broll.gainea.net.NT_Event_RemoveCard;
import com.broll.gainea.net.NT_Event_RemoveGoal;
import com.broll.gainea.net.NT_Event_TextInfo;
import com.broll.gainea.net.NT_Event_UpdateObjects;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.server.core.actions.optional.CardAction;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.PackageReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameEventSite extends AbstractGameSite {

    private final static Logger Log = LoggerFactory.getLogger(GameEventSite.class);

    private LogWindow logWindow() {
        return this.game.ui.inGameUI.getLogWindow();
    }

    @Override
    public void receive(Object object) {
        super.receive(object);
        NT_Event event = (NT_Event) object;
        if (event.screenEffect != 0) {

        }
        if (event.sound != null) {
            AudioPlayer.playSound(event.sound);
        }
    }

    @PackageReceiver
    public void received(NT_Event_TextInfo text) {
        if (text.type == NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY) {
            MessageUtils.showCenterMessage(game, text.text);
        } else {
            logWindow().log(text.text);
        }
    }

    @PackageReceiver
    public void received(NT_Event_ReceivedCard card) {
        game.state.getPlayer(getPlayer().getId()).cards++;
        game.ui.inGameUI.hideWindows();
        Table table = new Table(game.ui.skin);
        table.add(LabelUtils.label(game.ui.skin, "Du hast eine Aktionskarte erhalten:")).padBottom(20).row();
        table.add(CardWindow.renderCard(game, card.card));
        game.ui.inGameUI.showCenterOverlay(new Popup(game.ui.skin, table, 3f));
        game.state.getCards().add(card.card);
        game.ui.inGameUI.updateWindows();
        logWindow().logCardEvent("Du hast [VIOLET]" + card.card.title + "[] erhalten!");
    }

    @PackageReceiver
    public void received(NT_Event_FocusObject focus) {
        focus(focus.object);
    }

    @PackageReceiver
    public void received(NT_Event_FocusObjects focus) {
        focus(focus.objects);
    }

    @PackageReceiver
    public void received(NT_Event_UpdateObjects update) {
        GameUtils.updateMapObjects(game, update.objects);
        game.state.updateMapObjects();
    }

    private void focus(NT_BoardObject... objects) {
        game.ui.inGameUI.hideWindows();
        GameUtils.updateMapObjects(game, objects);
        MapScrollUtils.showObject(game, objects[0]);
        game.state.updateMapObjects();
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_MovedObject moved) {
        game.state.updateIdleState(false);
        game.ui.inGameUI.hideWindows();
        AudioPlayer.playSound("move.ogg");
        NT_BoardObject moveObject = moved.objects[0];
        int from = GameUtils.findObject(game, moveObject.id).location;
        int to = moveObject.location;
        MapScrollUtils.showLocations(game, from, to);
        GameUtils.updateMapObjects(game, moved.objects);
        boolean first = true;
        for (NT_BoardObject object : moved.objects) {
            MapObjectRender render = game.state.getMapObjectsContainer().getObjectRender(object);
            Location location = game.state.getMap().getLocation(object.location);
            MoveToAction action = new MoveToAction();
            action.setDuration(1);
            action.setPosition(location.getCoordinates().getDisplayX(), location.getCoordinates().getDisplayY() + render.getStackHeight());
            if (first) {
                render.addAction(Actions.sequence(action, Actions.run(() -> {
                    //walking done
                    game.state.updateMapObjects();
                })));
            } else {
                render.addAction(action);
            }
            first = false;
        }
    }

    @PackageReceiver
    public void received(NT_Event_PlacedObject placed) {
        game.ui.inGameUI.hideWindows();
        NT_BoardObject object = placed.object;
        MapScrollUtils.showObject(game, object);
        GameUtils.addMapObject(game, object);
        game.state.updateMapObjects();
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_PlayedCard card) {
        game.state.updateIdleState(false);
        game.ui.inGameUI.hideWindows();
        game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(CardWindow.renderCard(game, card.card), (float) CardAction.PLAY_CARD_DELAY / 1000f));
        NT_Player owner = game.state.getPlayer(card.player);
        if (owner != null) {
            owner.cards--;
        }
        if (card.player == getPlayer().getId()) {
            game.state.getCards().remove(card.card);
        }
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_FocusLocation location) {
        game.ui.inGameUI.hideWindows();
        MapScrollUtils.showLocations(game, location.location);
    }

    @PackageReceiver
    public void received(NT_Event_ReceivedGoal goal) {
        game.ui.inGameUI.hideWindows();
        Log.info("received goal");
        game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(GoalOverlay.renderGoal(game, goal.goal), 3));
        game.state.getGoals().add(goal.goal);
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_RemoveGoal goal) {
        Log.info("removed goal");
        game.state.getGoals().remove(goal.goal);
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_RemoveCard card) {
        Log.info("removed card");
        game.state.getCards().remove(card.card);
        NT_Player owner = game.state.getPlayer(card.player);
        owner.cards--;
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_OtherPlayerReceivedCard card) {
        game.ui.inGameUI.hideWindows();
        NT_Player owner = game.state.getPlayer(card.player);
        owner.cards++;
        String info = owner.name + " hat eine Karte erhalten!";
        game.ui.inGameUI.updateWindows();
        MessageUtils.showCenterMessage(game, info);
    }

    @PackageReceiver
    public void received(NT_Event_OtherPlayerReceivedGoal goal) {
        game.ui.inGameUI.hideWindows();
        NT_Player owner = game.state.getPlayer(goal.player);
        String info = owner.name + " hat ein neues Ziel erhalten!";
        MessageUtils.showCenterMessage(game, info);
    }

    @PackageReceiver
    public void received(NT_Event_ReceivedPoints points) {
        game.state.getPlayer(points.player).points += points.points;
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_ReceivedStars stars) {
        game.state.getPlayer(stars.player).stars += stars.stars;
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_FinishedGoal goal) {
        game.ui.inGameUI.hideWindows();
        boolean myGoal = goal.player == getPlayer().getId();
        if (myGoal) {
            game.state.getGoals().remove(goal.goal);
        }
        FinishedGoalDisplay message = new FinishedGoalDisplay(game, goal, myGoal);
        game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(message, 3));
    }

    @PackageReceiver
    public void received(NT_Event_BoardEffect nt) {
        GameUtils.updateMapEffects(game.state, nt.effects);
        game.state.updateMapObjects();
    }
}
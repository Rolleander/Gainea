package com.broll.gainea.client.network.sites;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.GameUtils;
import com.broll.gainea.client.ui.components.FinishedGoalDisplay;
import com.broll.gainea.client.ui.ingame.hud.GoalOverlay;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.map.MapScrollUtils;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.ingame.windows.LogWindow;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.MessageUtils;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Event;
import com.broll.gainea.net.NT_Event_BoardEffect;
import com.broll.gainea.net.NT_Event_BoughtMerc;
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
import com.broll.gainea.net.NT_Event_RemoveObjects;
import com.broll.gainea.net.NT_Event_TextInfo;
import com.broll.gainea.net.NT_Event_UpdateObjects;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.PackageReceiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

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
        if (game.ui.inGameUI == null) return;
        logWindow().log(text.text);
        if (text.type == NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY) {
            game.ui.inGameUI.infoMessages.show(text.text);
        } else if (text.type == NT_Event_TextInfo.TYPE_CONFIRM_MESSAGE) {
            MessageUtils.showConfirmMessage(game, text.text);
        }
    }

    @PackageReceiver
    public void received(NT_Event_ReceivedCard card) {
        game.state.getPlayer(getPlayer().getId()).cards++;
        game.ui.inGameUI.hideWindows();
        Table table = new Table(game.ui.skin);
        table.add(LabelUtils.label(game.ui.skin, "Du hast eine Aktionskarte erhalten:")).padBottom(20).row();
        table.add(CardWindow.renderCard(game, card.card));
        game.ui.inGameUI.infoMessages.showCardReceived(card.card);
        game.state.getCards().add(card.card);
        game.ui.inGameUI.updateWindows();
        logWindow().logCardEvent("Du hast [VIOLET]" + card.card.title + "[] erhalten!");
    }

    @PackageReceiver
    public void received(NT_Event_FocusObject focus) {
        focus(focus.screenEffect, focus.object);
    }

    @PackageReceiver
    public void received(NT_Event_FocusObjects focus) {
        focus(focus.screenEffect, focus.objects);
    }

    @PackageReceiver
    public void received(NT_Event_RemoveObjects evt) {
        for (NT_BoardObject obj : evt.objects) {
            game.state.getMapObjectsContainer().remove(obj);
        }
        game.state.getMapObjectsContainer().rearrangeStacks();
        GameUtils.removeObjects(game, evt.objects);
    }

    @PackageReceiver
    public void received(NT_Event_UpdateObjects update) {
        game.state.getMapObjectsContainer().update(update.screenEffect, Arrays.asList(update.objects));
        GameUtils.updateMapObjects(game, update.objects);
    }

    private void focus(int effect, NT_BoardObject... objects) {
        game.ui.inGameUI.hideWindows();
        game.state.getMapObjectsContainer().update(effect, Arrays.asList(objects));
        GameUtils.updateMapObjects(game, objects);
        MapScrollUtils.showObject(game, objects[0]);
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
                    NT_BoardObject nt = GameUtils.findObject(game, object.id);
                    if (nt != null) {
                        nt.location = (short) to;
                    }
                    game.state.updateMapObjects(NT_Event.EFFECT_MOVED);
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
        game.state.updateMapObjects(NT_Event.EFFECT_SPAWNED);
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_PlayedCard card) {
        game.state.updateIdleState(false);
        game.ui.inGameUI.hideWindows();
        game.ui.inGameUI.infoMessages.show(CardWindow.renderCard(game, card.card), 4f);
        if (card.card.playable) {
            NT_Player owner = game.state.getPlayer(card.player);
            if (owner != null) {
                owner.cards--;
            }
            if (card.player == getPlayer().getId()) {
                game.state.getCards().remove(card.card);
                logWindow().logCardEvent("Du hast [VIOLET]" + card.card.title + "[] ausgespielt: [GRAY]" + card.card.text + "[]");
            } else if (owner != null) {
                logWindow().logCardEvent(owner.name + " hat [VIOLET]" + card.card.title + "[] ausgespielt: [GRAY]" + card.card.text + "[]");
            }
        }
        if (card.player == -1 || !card.card.playable) {
            logWindow().logCardEvent("Event-Karte [VIOLET]" + card.card.title + "[] wurde aktiviert: [GRAY]" + card.card.text + "[]");
        }
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_FocusLocation location) {
        game.ui.inGameUI.hideWindows();
        MapScrollUtils.showLocations(game, location.location);
    }

    @PackageReceiver
    public void received(NT_Event_BoughtMerc nt) {
        NT_Player owner = game.state.getPlayer(nt.player);
        logWindow().logStarEvent(owner.name + " hat [ORANGE]" + nt.unit.name + "[] gekauft");
    }

    @PackageReceiver
    public void received(NT_Event_ReceivedGoal goal) {
        game.ui.inGameUI.hideWindows();
        Log.info("received goal");
        game.ui.inGameUI.infoMessages.show(GoalOverlay.renderGoal(game, goal.goal), 3f);
        game.state.getGoals().add(goal.goal);
        game.ui.inGameUI.updateWindows();
        logWindow().logGoalEvent("Neues Ziel erhalten: [BROWN]" + goal.goal.description + "[]");
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
        game.ui.inGameUI.infoMessages.show(info);
        logWindow().logCardEvent(owner.name + " hat eine Karte erhalten!");
    }

    @PackageReceiver
    public void received(NT_Event_OtherPlayerReceivedGoal goal) {
        game.ui.inGameUI.hideWindows();
        NT_Player owner = game.state.getPlayer(goal.player);
        String info = owner.name + " hat ein neues Ziel erhalten!";
        game.ui.inGameUI.infoMessages.show(info);
        logWindow().logGoalEvent(owner.name + " hat ein neues Ziel erhalten!");
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
            logWindow().logGoalEvent("Du hast hat dein Ziel erreicht: [BROWN]" + goal.goal.description + "[] (+" + goal.goal.points + " Punkte)");
        } else {
            NT_Player owner = game.state.getPlayer(goal.player);
            logWindow().logGoalEvent(owner.name + " hat ein Ziel erreicht: [BROWN]" + goal.goal.description + "[] (+" + goal.goal.points + " Punkte)");
        }
        FinishedGoalDisplay message = new FinishedGoalDisplay(game, goal, myGoal);
        game.ui.inGameUI.infoMessages.show(message, 4f);
    }

    @PackageReceiver
    public void received(NT_Event_BoardEffect nt) {
        GameUtils.updateMapEffects(game.state, nt.effects);
        game.state.updateMapObjects(nt.screenEffect);
    }
}
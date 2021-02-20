package com.broll.gainea.client.game.sites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.game.GameUtils;
import com.broll.gainea.client.game.MapScrollUtils;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.MessageUtils;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.ingame.windows.GoalWindow;
import com.broll.gainea.client.ui.elements.render.MapObjectRender;
import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Event_Bundle;
import com.broll.gainea.net.NT_Event_FinishedGoal;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedCard;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedGoal;
import com.broll.gainea.net.NT_Event_ReceivedCard;
import com.broll.gainea.net.NT_Event_FocusLocation;
import com.broll.gainea.net.NT_Event_FocusObject;
import com.broll.gainea.net.NT_Event_MovedObject;
import com.broll.gainea.net.NT_Event_PlacedObject;
import com.broll.gainea.net.NT_Event_PlayedCard;
import com.broll.gainea.net.NT_Event_ReceivedGoal;
import com.broll.gainea.net.NT_Event_TextInfo;
import com.broll.gainea.server.core.map.Location;
import com.broll.networklib.PackageReceiver;
import com.esotericsoftware.minlog.Log;

public class GameEventSite extends AbstractGameSite {


    @Override
    public void receive(Object object) {
        super.receive(object);
        NT_Abstract_Event event = (NT_Abstract_Event) object;
        if (event.screenEffect != 0) {

        }
        if (event.sound != null) {
            game.assets.get(event.sound, Sound.class).play();
        }
    }

    @PackageReceiver
    public void received(NT_Event_Bundle bundle) {

    }

    @PackageReceiver
    public void received(NT_Event_TextInfo text) {
        if (text.type == NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY) {
            MessageUtils.showCenterMessage(game, text.text);
        } else {
            //just for eventlog window
        }
    }

    @PackageReceiver
    public void received(NT_Event_ReceivedCard card) {
        game.ui.inGameUI.hideWindows();
        game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(CardWindow.renderCard(game, card.card), 3));
        game.state.getCards().add(card.card);
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_FocusObject focus) {
        game.ui.inGameUI.hideWindows();
        GameUtils.updateMapObjects(game, focus.object);
        MapScrollUtils.showObject(game, focus.object);
        game.state.updateMapObjects();
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_MovedObject moved) {
        game.ui.inGameUI.hideWindows();
        game.assets.get("sounds/move.ogg", Sound.class).play();
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
            action.setPosition(location.getCoordinates().getDisplayX(), location.getCoordinates().getDisplayY());
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
        game.ui.inGameUI.hideWindows();
        game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(CardWindow.renderCard(game, card.card), 3));
        if (card.player == getPlayer().getId()) {
            game.state.getCards().remove(card.card);
            game.ui.inGameUI.updateWindows();
        }
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
        game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(GoalWindow.renderGoal(game.ui.skin, goal.goal), 3));
        game.state.getGoals().add(goal.goal);
        game.ui.inGameUI.updateWindows();
    }

    @PackageReceiver
    public void received(NT_Event_OtherPlayerReceivedCard card) {
        game.ui.inGameUI.hideWindows();
        String info = game.state.getPlayer(card.player).name + " hat eine Karte erhalten!";
        MessageUtils.showActionMessage(game, info);
    }

    @PackageReceiver
    public void received(NT_Event_OtherPlayerReceivedGoal goal) {
        game.ui.inGameUI.hideWindows();
        String info = game.state.getPlayer(goal.player).name + " hat ein neues Ziel erhalten!";
        MessageUtils.showActionMessage(game, info);
    }

    @PackageReceiver
    public void received(NT_Event_FinishedGoal goal) {
        game.ui.inGameUI.hideWindows();
        String text = "";
        if (goal.player == getPlayer().getId()) {
            text = "Du hast ein Ziel erreicht!";
            game.state.getGoals().remove(goal.goal);
        } else {
            text = game.state.getPlayer(goal.player).name + " hat ein Ziel erreicht!";
        }
        Table message = new Table(game.ui.skin);
        message.setBackground("highlight");
        message.pad(20, 10, 20, 10);
        message.defaults().space(50);
        message.add(LabelUtils.label(game.ui.skin, text)).center().row();
        message.add(GoalWindow.renderGoal(game.ui.skin, goal.goal));
        game.ui.inGameUI.showCenterOverlay(TableUtils.removeAfter(message, 3));
    }
}
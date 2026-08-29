package com.broll.gainea.client.network.sites;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.GameUtils;
import com.broll.gainea.client.network.ProtoMapper;
import com.broll.gainea.client.ui.components.FinishedGoalDisplay;
import com.broll.gainea.client.ui.ingame.hud.GoalOverlay;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.map.MapScrollUtils;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.ingame.windows.LogWindow;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.MessageUtils;
import com.broll.gainea.net.*;
import com.broll.gainea.server.core.map.Location;
import com.broll.mpnll.client.site.MessageReceiverRegistry;
import com.google.protobuf.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.function.Consumer;

public class GameEventSite extends AbstractGameSite {
    private static final Logger Log = LoggerFactory.getLogger(GameEventSite.class);

    private LogWindow logWindow() {
        return game.ui.inGameUI.getLogWindow();
    }

    @Override
    protected void registerReceivers(MessageReceiverRegistry r) {
        event(r, com.broll.gainea.net.proto.NT_Event_TextInfo.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_ReceivedCard.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_FocusObject.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_FocusObjects.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_RemoveObjects.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_UpdateObjects.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_MovedObject.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_PlacedObject.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_PlayedCard.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_FocusLocation.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_BoughtMerc.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_ReceivedGoal.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_RemoveGoal.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_RemoveCard.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_OtherPlayerReceivedCard.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_OtherPlayerReceivedGoal.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_ReceivedPoints.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_ReceivedStars.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_FinishedGoal.class, this::received);
        event(r, com.broll.gainea.net.proto.NT_Event_BoardEffect.class, this::received);
    }

    private <T extends Message> void event(MessageReceiverRegistry r, Class<T> type, Consumer<T> receiver) {
        on(r, type, message -> {
            playEvent(message);
            receiver.accept(message);
        });
    }

    private void playEvent(Message message) {
        com.google.protobuf.Descriptors.FieldDescriptor sound = message.getDescriptorForType().findFieldByName("sound");
        if (sound != null && message.hasField(sound)) {
            String value = (String) message.getField(sound);
            if (!value.isEmpty()) AudioPlayer.playSound(value);
        }
    }

    private void received(com.broll.gainea.net.proto.NT_Event_TextInfo text) {
        if (game.ui.inGameUI == null) return;
        logWindow().log(text.getText());
        if (text.getType() == NT_Event_TextInfo.TYPE_MESSAGE_DISPLAY)
            game.ui.inGameUI.infoMessages.show(text.getText());
        else if (text.getType() == NT_Event_TextInfo.TYPE_CONFIRM_MESSAGE)
            MessageUtils.showConfirmMessage(game, text.getText());
    }

    private void received(com.broll.gainea.net.proto.NT_Event_ReceivedCard value) {
        NT_Card card = ProtoMapper.card(value.getCard());
        game.state.getPlayer(getPlayer().getId()).cards++;
        game.ui.inGameUI.hideWindows();
        Table table = new Table(game.ui.skin);
        table.add(LabelUtils.label(game.ui.skin, "Du hast eine Aktionskarte erhalten:")).padBottom(20).row();
        table.add(CardWindow.renderCard(game, card));
        game.ui.inGameUI.infoMessages.showCardReceived(card);
        game.state.getCards().add(card);
        game.ui.inGameUI.updateWindows();
        logWindow().logCardEvent("Du hast [VIOLET]" + card.title + "[] erhalten!");
    }

    private void received(com.broll.gainea.net.proto.NT_Event_FocusObject value) {
        focus(value.getScreenEffect(), ProtoMapper.boardObject(value.getObject()));
    }

    private void received(com.broll.gainea.net.proto.NT_Event_FocusObjects value) {
        focus(value.getScreenEffect(), ProtoMapper.boardObjects(value.getObjectsList()));
    }

    private void received(com.broll.gainea.net.proto.NT_Event_RemoveObjects value) {
        NT_BoardObject[] objects = ProtoMapper.boardObjects(value.getObjectsList());
        for (NT_BoardObject obj : objects) game.state.getMapObjectsContainer().remove(obj);
        game.state.getMapObjectsContainer().rearrangeStacks();
        GameUtils.removeObjects(game, objects);
    }

    private void received(com.broll.gainea.net.proto.NT_Event_UpdateObjects value) {
        NT_BoardObject[] objects = ProtoMapper.boardObjects(value.getObjectsList());
        game.state.getMapObjectsContainer().update(value.getScreenEffect(), Arrays.asList(objects));
        GameUtils.updateMapObjects(game, objects);
    }

    private void focus(int effect, NT_BoardObject... objects) {
        game.ui.inGameUI.hideWindows();
        game.state.getMapObjectsContainer().update(effect, Arrays.asList(objects));
        GameUtils.updateMapObjects(game, objects);
        MapScrollUtils.showObject(game, objects[0]);
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_MovedObject value) {
        NT_BoardObject[] objects = ProtoMapper.boardObjects(value.getObjectsList());
        game.state.updateIdleState(false);
        game.ui.inGameUI.hideWindows();
        AudioPlayer.playSound("move.ogg");
        NT_BoardObject moveObject = objects[0];
        int from = GameUtils.findObject(game, moveObject.id).location, to = moveObject.location;
        MapScrollUtils.showLocations(game, from, to);
        GameUtils.updateMapObjects(game, objects);
        boolean first = true;
        for (NT_BoardObject object : objects) {
            MapObjectRender render = game.state.getMapObjectsContainer().getObjectRender(object);
            Location location = game.state.getMap().getLocation(object.location);
            MoveToAction action = new MoveToAction();
            action.setDuration(1);
            action.setPosition(location.getCoordinates().getDisplayX(), location.getCoordinates().getDisplayY() + render.getStackHeight());
            if (first) render.addAction(Actions.sequence(action, Actions.run(() -> {
                NT_BoardObject nt = GameUtils.findObject(game, object.id);
                if (nt != null) nt.location = (short) to;
                game.state.updateMapObjects(NT_Event.EFFECT_MOVED);
            })));
            else render.addAction(action);
            first = false;
        }
    }

    private void received(com.broll.gainea.net.proto.NT_Event_PlacedObject value) {
        NT_BoardObject object = ProtoMapper.boardObject(value.getObject());
        game.ui.inGameUI.hideWindows();
        MapScrollUtils.showObject(game, object);
        GameUtils.addMapObject(game, object);
        game.state.updateMapObjects(NT_Event.EFFECT_SPAWNED);
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_PlayedCard value) {
        NT_Card card = ProtoMapper.card(value.getCard());
        int player = value.getPlayer();
        game.state.updateIdleState(false);
        game.ui.inGameUI.hideWindows();
        game.ui.inGameUI.infoMessages.show(CardWindow.renderCard(game, card), 4f);
        if (card.playable) {
            NT_Player owner = game.state.getPlayer(player);
            if (owner != null) owner.cards--;
            if (player == getPlayer().getId()) {
                game.state.getCards().remove(card);
                logWindow().logCardEvent("Du hast [VIOLET]" + card.title + "[] ausgespielt: [GRAY]" + card.text + "[]");
            } else if (owner != null)
                logWindow().logCardEvent(owner.name + " hat [VIOLET]" + card.title + "[] ausgespielt: [GRAY]" + card.text + "[]");
        }
        if (player == -1 || !card.playable)
            logWindow().logCardEvent("Event-Karte [VIOLET]" + card.title + "[] wurde aktiviert: [GRAY]" + card.text + "[]");
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_FocusLocation value) {
        game.ui.inGameUI.hideWindows();
        MapScrollUtils.showLocations(game, value.getLocation());
    }

    private void received(com.broll.gainea.net.proto.NT_Event_BoughtMerc value) {
        NT_Player owner = game.state.getPlayer(value.getPlayer());
        logWindow().logStarEvent(owner.name + " hat [ORANGE]" + ProtoMapper.unit(value.getUnit()).name + "[] gekauft");
    }

    private void received(com.broll.gainea.net.proto.NT_Event_ReceivedGoal value) {
        NT_Goal goal = ProtoMapper.goal(value.getGoal());
        game.ui.inGameUI.hideWindows();
        Log.info("received goal");
        game.ui.inGameUI.infoMessages.show(GoalOverlay.renderGoal(game, goal), 3f);
        game.state.getGoals().add(goal);
        game.ui.inGameUI.updateWindows();
        logWindow().logGoalEvent("Neues Ziel erhalten: [BROWN]" + goal.description + "[]");
    }

    private void received(com.broll.gainea.net.proto.NT_Event_RemoveGoal value) {
        NT_Goal goal = ProtoMapper.goal(value.getGoal());
        Log.info("removed goal");
        game.state.getGoals().remove(goal);
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_RemoveCard value) {
        NT_Card card = ProtoMapper.card(value.getCard());
        Log.info("removed card");
        game.state.getCards().remove(card);
        game.state.getPlayer(value.getPlayer()).cards--;
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_OtherPlayerReceivedCard value) {
        game.ui.inGameUI.hideWindows();
        NT_Player owner = game.state.getPlayer(value.getPlayer());
        owner.cards++;
        game.ui.inGameUI.updateWindows();
        game.ui.inGameUI.infoMessages.show(owner.name + " hat eine Karte erhalten!");
        logWindow().logCardEvent(owner.name + " hat eine Karte erhalten!");
    }

    private void received(com.broll.gainea.net.proto.NT_Event_OtherPlayerReceivedGoal value) {
        game.ui.inGameUI.hideWindows();
        NT_Player owner = game.state.getPlayer(value.getPlayer());
        String info = owner.name + " hat ein neues Ziel erhalten!";
        game.ui.inGameUI.infoMessages.show(info);
        logWindow().logGoalEvent(info);
    }

    private void received(com.broll.gainea.net.proto.NT_Event_ReceivedPoints value) {
        game.state.getPlayer(value.getPlayer()).points += value.getPoints();
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_ReceivedStars value) {
        game.state.getPlayer(value.getPlayer()).stars += value.getStars();
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_FinishedGoal value) {
        NT_Goal goal = ProtoMapper.goal(value.getGoal());
        boolean mine = value.getPlayer() == getPlayer().getId();
        game.ui.inGameUI.hideWindows();
        if (mine) {
            game.state.getGoals().remove(goal);
            logWindow().logGoalEvent("Du hast hat dein Ziel erreicht: [BROWN]" + goal.description + "[] (+" + goal.points + " Punkte)");
        } else {
            NT_Player owner = game.state.getPlayer(value.getPlayer());
            logWindow().logGoalEvent(owner.name + " hat ein Ziel erreicht: [BROWN]" + goal.description + "[] (+" + goal.points + " Punkte)");
        }
        NT_Event_FinishedGoal event = new NT_Event_FinishedGoal();
        event.player = value.getPlayer();
        event.goal = goal;
        event.screenEffect = value.getScreenEffect();
        event.sound = value.getSound();
        game.ui.inGameUI.infoMessages.show(new FinishedGoalDisplay(game, event, mine), 4f);
    }

    private void received(com.broll.gainea.net.proto.NT_Event_BoardEffect value) {
        NT_BoardEffect[] effects = ProtoMapper.boardEffects(value.getEffectsList());
        GameUtils.updateMapEffects(game.state, effects);
        game.state.updateMapObjects(value.getScreenEffect());
    }
}

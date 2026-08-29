package com.broll.gainea.client.network.sites;

import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.GameUtils;
import com.broll.gainea.client.game.BoardObjectState;
import com.broll.gainea.client.game.GoalState;
import com.broll.gainea.client.game.PlayerState;
import com.broll.gainea.core.protocol.ScreenEffect;
import com.broll.gainea.core.protocol.TextInfoType;
import com.broll.gainea.client.ui.components.FinishedGoalDisplay;
import com.broll.gainea.client.ui.ingame.hud.GoalOverlay;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.map.MapScrollUtils;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.ingame.windows.LogWindow;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.MessageUtils;
import com.broll.gainea.net.proto.*;
import com.broll.gainea.client.game.map.Location;
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
        if (text.getType() == TextInfoType.DISPLAY.value)
            game.ui.inGameUI.infoMessages.show(text.getText());
        else if (text.getType() == TextInfoType.CONFIRM.value)
            MessageUtils.showConfirmMessage(game, text.getText());
    }

    private void received(com.broll.gainea.net.proto.NT_Event_ReceivedCard value) {
        NT_Card card = value.getCard();
        game.state.getPlayer(getPlayer().getId()).addCards(1);
        game.ui.inGameUI.hideWindows();
        Table table = new Table(game.ui.skin);
        table.add(LabelUtils.label(game.ui.skin, "Du hast eine Aktionskarte erhalten:")).padBottom(20).row();
        table.add(CardWindow.renderCard(game, card));
        game.ui.inGameUI.infoMessages.showCardReceived(card);
        game.state.getCards().add(card);
        game.ui.inGameUI.updateWindows();
        logWindow().logCardEvent("Du hast [VIOLET]" + card.getTitle() + "[] erhalten!");
    }

    private void received(com.broll.gainea.net.proto.NT_Event_FocusObject value) {
        focus(value.getScreenEffect(), java.util.Collections.singletonList(value.getObject()));
    }

    private void received(com.broll.gainea.net.proto.NT_Event_FocusObjects value) {
        focus(value.getScreenEffect(), value.getObjectsList());
    }

    private void received(com.broll.gainea.net.proto.NT_Event_RemoveObjects value) {
        for (NT_BoardObject message : value.getObjectsList()) {
            BoardObjectState obj = GameUtils.findObject(game, objectId(message));
            if (obj != null) game.state.getMapObjectsContainer().remove(obj);
        }
        game.state.getMapObjectsContainer().rearrangeStacks();
        GameUtils.removeObjects(game, value.getObjectsList());
    }

    private void received(com.broll.gainea.net.proto.NT_Event_UpdateObjects value) {
        GameUtils.updateMapObjects(game, value.getObjectsList());
        game.state.getMapObjectsContainer().update(value.getScreenEffect(), states(value.getObjectsList()));
    }

    private void focus(int effect, java.util.List<NT_BoardObject> objects) {
        game.ui.inGameUI.hideWindows();
        GameUtils.updateMapObjects(game, objects);
        java.util.List<BoardObjectState> states = states(objects);
        game.state.getMapObjectsContainer().update(effect, states);
        MapScrollUtils.showObject(game, states.get(0));
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_MovedObject value) {
        java.util.List<NT_BoardObject> objects = value.getObjectsList();
        game.state.updateIdleState(false);
        game.ui.inGameUI.hideWindows();
        AudioPlayer.playSound("move.ogg");
        NT_BoardObject moveObject = objects.get(0);
        int id = objectId(moveObject);
        int from = GameUtils.findObject(game, id).location(), to = objectLocation(moveObject);
        MapScrollUtils.showLocations(game, from, to);
        GameUtils.updateMapObjects(game, objects);
        boolean first = true;
        for (NT_BoardObject message : objects) {
            BoardObjectState object = GameUtils.findObject(game, objectId(message));
            MapObjectRender render = game.state.getMapObjectsContainer().getObjectRender(object);
            Location location = game.state.getMap().getLocation(object.location());
            MoveToAction action = new MoveToAction();
            action.setDuration(1);
            action.setPosition(location.getCoordinates().getDisplayX(), location.getCoordinates().getDisplayY() + render.getStackHeight());
            if (first) render.addAction(Actions.sequence(action, Actions.run(() -> {
                game.state.updateMapObjects(ScreenEffect.MOVED.value);
            })));
            else render.addAction(action);
            first = false;
        }
    }

    private void received(com.broll.gainea.net.proto.NT_Event_PlacedObject value) {
        BoardObjectState object = GameUtils.addMapObject(game, value.getObject());
        game.ui.inGameUI.hideWindows();
        MapScrollUtils.showObject(game, object);
        game.state.updateMapObjects(ScreenEffect.SPAWNED.value);
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_PlayedCard value) {
        NT_Card card = value.getCard();
        int player = value.getPlayer();
        game.state.updateIdleState(false);
        game.ui.inGameUI.hideWindows();
        game.ui.inGameUI.infoMessages.show(CardWindow.renderCard(game, card), 4f);
        if (card.getPlayable()) {
            PlayerState owner = game.state.getPlayer(player);
            if (owner != null) owner.addCards(-1);
            if (player == getPlayer().getId()) {
                game.state.getCards().remove(card);
                logWindow().logCardEvent("Du hast [VIOLET]" + card.getTitle() + "[] ausgespielt: [GRAY]" + card.getText() + "[]");
            } else if (owner != null)
                logWindow().logCardEvent(owner.name() + " hat [VIOLET]" + card.getTitle() + "[] ausgespielt: [GRAY]" + card.getText() + "[]");
        }
        if (player == -1 || !card.getPlayable())
            logWindow().logCardEvent("Event-Karte [VIOLET]" + card.getTitle() + "[] wurde aktiviert: [GRAY]" + card.getText() + "[]");
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_FocusLocation value) {
        game.ui.inGameUI.hideWindows();
        MapScrollUtils.showLocations(game, value.getLocation());
    }

    private void received(com.broll.gainea.net.proto.NT_Event_BoughtMerc value) {
        PlayerState owner = game.state.getPlayer(value.getPlayer());
        logWindow().logStarEvent(owner.name() + " hat [ORANGE]" + value.getUnit().getName() + "[] gekauft");
    }

    private void received(com.broll.gainea.net.proto.NT_Event_ReceivedGoal value) {
        NT_Goal goal = value.getGoal();
        game.ui.inGameUI.hideWindows();
        Log.info("received goal");
        game.ui.inGameUI.infoMessages.show(GoalOverlay.renderGoal(game, goal), 3f);
        game.state.getGoals().add(new GoalState(goal));
        game.ui.inGameUI.updateWindows();
        logWindow().logGoalEvent("Neues Ziel erhalten: [BROWN]" + goal.getDescription() + "[]");
    }

    private void received(com.broll.gainea.net.proto.NT_Event_RemoveGoal value) {
        GoalState goal = new GoalState(value.getGoal());
        Log.info("removed goal");
        game.state.getGoals().remove(goal);
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_RemoveCard value) {
        NT_Card card = value.getCard();
        Log.info("removed card");
        game.state.getCards().remove(card);
        game.state.getPlayer(value.getPlayer()).addCards(-1);
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_OtherPlayerReceivedCard value) {
        game.ui.inGameUI.hideWindows();
        PlayerState owner = game.state.getPlayer(value.getPlayer());
        owner.addCards(1);
        game.ui.inGameUI.updateWindows();
        game.ui.inGameUI.infoMessages.show(owner.name() + " hat eine Karte erhalten!");
        logWindow().logCardEvent(owner.name() + " hat eine Karte erhalten!");
    }

    private void received(com.broll.gainea.net.proto.NT_Event_OtherPlayerReceivedGoal value) {
        game.ui.inGameUI.hideWindows();
        PlayerState owner = game.state.getPlayer(value.getPlayer());
        String info = owner.name() + " hat ein neues Ziel erhalten!";
        game.ui.inGameUI.infoMessages.show(info);
        logWindow().logGoalEvent(info);
    }

    private void received(com.broll.gainea.net.proto.NT_Event_ReceivedPoints value) {
        game.state.getPlayer(value.getPlayer()).addPoints(value.getPoints());
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_ReceivedStars value) {
        game.state.getPlayer(value.getPlayer()).addStars(value.getStars());
        game.ui.inGameUI.updateWindows();
    }

    private void received(com.broll.gainea.net.proto.NT_Event_FinishedGoal value) {
        NT_Goal goal = value.getGoal();
        boolean mine = value.getPlayer() == getPlayer().getId();
        game.ui.inGameUI.hideWindows();
        if (mine) {
            game.state.getGoals().remove(new GoalState(goal));
            logWindow().logGoalEvent("Du hast hat dein Ziel erreicht: [BROWN]" + goal.getDescription() + "[] (+" + goal.getPoints() + " Punkte)");
        } else {
            PlayerState owner = game.state.getPlayer(value.getPlayer());
            logWindow().logGoalEvent(owner.name() + " hat ein Ziel erreicht: [BROWN]" + goal.getDescription() + "[] (+" + goal.getPoints() + " Punkte)");
        }
        game.ui.inGameUI.infoMessages.show(new FinishedGoalDisplay(game, value, mine), 4f);
    }

    private void received(com.broll.gainea.net.proto.NT_Event_BoardEffect value) {
        GameUtils.updateMapEffects(game.state, value.getEffectsList());
        game.state.updateMapObjects(value.getScreenEffect());
    }

    private java.util.List<BoardObjectState> states(java.util.List<NT_BoardObject> messages) {
        java.util.List<BoardObjectState> result = new java.util.ArrayList<>();
        messages.forEach(message -> result.add(GameUtils.findObject(game, objectId(message))));
        return result;
    }

    private static int objectId(NT_BoardObject value) {
        return value.hasMonster() ? value.getMonster().getId() : value.hasUnit() ? value.getUnit().getId() : value.getId();
    }

    private static int objectLocation(NT_BoardObject value) {
        return value.hasMonster() ? value.getMonster().getLocation() : value.hasUnit() ? value.getUnit().getLocation() : value.getLocation();
    }
}

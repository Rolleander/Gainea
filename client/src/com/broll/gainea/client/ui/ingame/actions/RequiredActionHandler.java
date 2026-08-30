package com.broll.gainea.client.ui.ingame.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.BoardObjectState;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.ui.components.Popup;
import com.broll.gainea.client.ui.ingame.hud.GoalOverlay;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.net.proto.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RequiredActionHandler {

    private Gainea game;
    private Skin skin;
    private Table window;
    private List<MapAction> mapActions = new ArrayList<>();

    public RequiredActionHandler(Gainea game, Skin skin) {
        this.game = game;
        this.skin = skin;
    }

    public void toFront() {
        mapActions.forEach(it -> it.setZIndex(10000));
    }

    public void handleAction(NT_Action action, PlayerPerformAction playerPerformAction) {
        mapActions.clear();
        boolean center = true;
        AudioPlayer.playSound("place.ogg");
        RequiredActionContainer container = new RequiredActionContainer(mapActions, playerPerformAction, this::close);
        if (action.hasPlaceUnit()) {
            window = PlaceUnitWindow.create(game, skin, container, action);
            center = false;
        } else if (action.hasSelectChoice()) {
            handleSelectionAction(action, container);
        }
        mapActions.forEach(it -> game.gameStage.addActor(it));
        if (window != null) {
            if (center) {
                game.ui.inGameUI.showCenter(new Popup(skin, window));
            } else {
                game.ui.inGameUI.showBottomCenter(new Popup(skin, window));
            }
        }
    }

    private void handleSelectionAction(NT_Action wrappedAction, RequiredActionContainer container) {
        NT_Action_SelectChoice action = wrappedAction.getSelectChoice();
        List<Actor> options = null;
        if (action.getObjectChoicesCount() > 0) {
            //other selection
            NT_Choice objectChoice = action.getObjectChoices(0);
            if (objectChoice.hasIntegerValue()) {
                //location
                PlaceUnitWindow.showLocationMapActions(game, action.getObjectChoicesList().stream().mapToInt(NT_Choice::getIntegerValue).toArray(), wrappedAction, container);
                options = new ArrayList<>();
            } else if (objectChoice.hasGoal()) {
                options = action.getObjectChoicesList().stream().map(choice -> GoalOverlay.renderGoal(game, choice.getGoal())).collect(Collectors.toList());
            } else if (objectChoice.hasCard()) {
                options = action.getObjectChoicesList().stream().map(choice -> CardWindow.renderCard(game, choice.getCard())).collect(Collectors.toList());
            } else if (objectChoice.hasUnit() || objectChoice.hasBoardObject()) {
                options = action.getObjectChoicesList().stream().map(choice -> MapObjectRender.createRender(game, skin,
                    new BoardObjectState(choice.hasBoardObject() ? choice.getBoardObject() : NT_BoardObject.newBuilder().setUnit(choice.getUnit()).build()))).collect(Collectors.toList());
            }
        } else {
            //text selection
            options = action.getChoicesList().stream().map(choice -> LabelUtils.label(game.ui.skin, choice)).collect(Collectors.toList());
        }
        window = SelectOptionWindow.create(game, container, wrappedAction, options);
    }

    private void close() {
        game.ui.inGameUI.clearCenter();
        mapActions.forEach(Actor::remove);
        mapActions.clear();
        if (window != null) {
            window.remove();
        }
        window = null;
    }


}

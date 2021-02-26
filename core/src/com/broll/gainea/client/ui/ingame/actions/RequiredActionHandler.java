package com.broll.gainea.client.ui.ingame.actions;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.client.ui.components.Popup;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.windows.CardWindow;
import com.broll.gainea.client.ui.ingame.windows.GoalWindow;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Action_SelectChoice;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Unit;

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

    public void handleAction(NT_Action action, PlayerPerformAction playerPerformAction) {
        mapActions.clear();
        RequiredActionContainer container = new RequiredActionContainer(mapActions, playerPerformAction, this::close);
        if (action instanceof NT_Action_PlaceUnit) {
            window = PlaceUnitWindow.create(game, skin, container, (NT_Action_PlaceUnit) action);
        } else if (action instanceof NT_Action_SelectChoice) {
            handleSelectionAction((NT_Action_SelectChoice) action, container);
        }
        mapActions.forEach(it -> game.gameStage.addActor(it));
        if (window != null) {
            game.ui.inGameUI.showCenter(new Popup(skin, window)).padTop(150);
        }
    }

    private void handleSelectionAction(NT_Action_SelectChoice action, RequiredActionContainer container) {
        List<Actor> options = null;
        if (action.choices == null) {
            //other selection
            Object objectChoice = action.objectChoices[0];
            if (objectChoice instanceof Integer) {
                //location
                PlaceUnitWindow.showLocationMapActions(game, Arrays.stream(action.objectChoices).mapToInt(choice -> (Integer) choice).toArray(), action, container);
                return;
            } else if (objectChoice instanceof NT_Goal) {
                options = Arrays.stream(action.objectChoices).map(choice -> GoalWindow.renderGoal(game.ui.skin, (NT_Goal) choice)).collect(Collectors.toList());
            } else if (objectChoice instanceof NT_Card) {
                options = Arrays.stream(action.objectChoices).map(choice -> CardWindow.renderCard(game, (NT_Card) choice)).collect(Collectors.toList());
            } else if (objectChoice instanceof NT_Unit) {
                options = Arrays.stream(action.objectChoices).map(choice -> MapObjectRender.createRender(game, skin, (NT_Unit) choice)).collect(Collectors.toList());
            }
        } else {
            //text selection
            options = Arrays.stream(action.choices).map(choice -> LabelUtils.label(game.ui.skin, choice)).collect(Collectors.toList());
        }
        window = SelectOptionWindow.create(game, container, action, options);
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

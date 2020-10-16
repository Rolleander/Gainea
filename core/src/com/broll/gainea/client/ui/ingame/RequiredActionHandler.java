package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.PlayerPerformAction;
import com.broll.gainea.client.ui.elements.MapAction;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Action_SelectChoice;

import java.util.ArrayList;
import java.util.List;

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
        if (action instanceof NT_Action_PlaceUnit) {
            window = PlaceUnitWindow.create(game, skin, mapActions, (NT_Action_PlaceUnit) action);
        } else if (action instanceof NT_Action_SelectChoice) {

        }
        if (window != null) {
            mapActions.forEach(it -> game.gameStage.addActor(it));
            game.ui.inGameUI.showCenter(window);
        }
    }

    private void close() {
        mapActions.forEach(Actor::remove);
        mapActions.clear();
        window.remove();
        window = null;
    }

}

package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.render.MapObjectRender;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.MapAction;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Unit;

import java.util.Arrays;
import java.util.List;

public class PlaceUnitWindow {

    public static Table create(Gainea game, Skin skin, List<MapAction> mapActions, NT_Action_PlaceUnit action) {
        NT_Unit unit = action.unitToPlace;
        Table window = new Table(skin);
        window.pad(30, 20, 10, 20);
        window.setBackground("menu-bg");
        MapObjectRender render = MapObjectRender.createRender(game,skin, unit);
        window.add(render).spaceRight(10);
        window.add(LabelUtils.label(skin, unit.name));
        Arrays.stream(action.possibleLocations).mapToObj(it -> game.state.getMap().getLocation(it)).forEach(location -> {
            MapAction mapAction = new MapAction(game, 2, location.getNumber(), () -> {

            });
            mapAction.setVisible(true);
            mapAction.setPosition(location.getCoordinates().getDisplayX(), location.getCoordinates().getDisplayY());
            mapActions.add(mapAction);
        });
        TableUtils.consumeClicks(window);
        return window;
    }
}

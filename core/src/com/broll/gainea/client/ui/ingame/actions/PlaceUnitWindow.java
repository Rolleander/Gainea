package com.broll.gainea.client.ui.ingame.actions;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.MapScrollUtils;
import com.broll.gainea.client.ui.elements.render.MapObjectRender;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.MapAction;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

public class PlaceUnitWindow {

    public static Table create(Gainea game, Skin skin, RequiredActionContainer container, NT_Action_PlaceUnit action) {
        NT_Unit unit = action.unitToPlace;
        Table window = new Table(skin);
        window.pad(30, 20, 10, 20);
        window.setBackground("menu-bg");
        MapObjectRender render = MapObjectRender.createRender(game, skin, unit);
        window.add(render).spaceRight(10);
        window.add(LabelUtils.label(skin, unit.name));
        showLocationMapActions(game, action.possibleLocations, action, container);
        MapScrollUtils.showLocations(game, action.possibleLocations);
        TableUtils.consumeClicks(window);
        return window;
    }

    public static void showLocationMapActions(Gainea game, int[] locations, NT_Action action, RequiredActionContainer container) {
        for (int i = 0; i < locations.length; i++) {
            Location location = game.state.getMap().getLocation(locations[i]);
            Integer optionIndex = new Integer(i);
            MapAction mapAction = new MapAction(game, 2, location.getNumber(), () ->
                    container.reactionResult(action, optionIndex.intValue())
            );
            mapAction.setVisible(true);
            mapAction.setPosition(location.getCoordinates().getDisplayX(), location.getCoordinates().getDisplayY());
            container.showMapAction(mapAction);
        }
    }
}

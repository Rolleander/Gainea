package com.broll.gainea.client.ui.ingame.actions;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.BoardObjectState;
import com.broll.gainea.client.network.sites.GameActionSite;
import com.broll.gainea.client.ui.ingame.map.MapAction;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.map.MapScrollUtils;
import com.broll.gainea.client.ui.utils.ArrayConversionUtils;
import com.broll.gainea.client.ui.utils.ColorUtils;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.net.proto.NT_Action;
import com.broll.gainea.net.proto.NT_Action_PlaceUnit;
import com.broll.gainea.net.proto.NT_BoardObject;
import com.broll.gainea.client.game.map.Location;

public class PlaceUnitWindow {

    public static Table create(Gainea game, Skin skin, RequiredActionContainer container, NT_Action wrappedAction) {
        NT_Action_PlaceUnit action = wrappedAction.getPlaceUnit();
        BoardObjectState unit = new BoardObjectState(NT_BoardObject.newBuilder().setUnit(action.getUnitToPlace()).build());
        Table window = new Table(skin);
        window.pad(30, 20, 10, 20);
        MapObjectRender render = MapObjectRender.createRender(game, skin, unit);
        if (GameActionSite.CURRENT_PLAYER_ACTION.hasText()) {
            window.add(actionLabel(skin)).center().spaceBottom(30).row();
        }
        Table content = new Table();
        content.add(render).spaceRight(30);
        content.add(LabelUtils.label(skin, unit.name()));
        window.add(content).center();
        int[] locations = action.getPossibleLocationsList().stream().mapToInt(Integer::intValue).toArray();
        showLocationMapActions(game, locations, wrappedAction, container);
        MapScrollUtils.showLocations(game, locations);
        return window;
    }

    public static Label actionLabel(Skin skin) {
        Label label = LabelUtils.color(LabelUtils.label(skin, GameActionSite.CURRENT_PLAYER_ACTION.getText()),
                ColorUtils.darker(Color.ROYAL, 0.2f));
        label.setFontScale(0.8f);
        return label;
    }

    public static void showLocationMapActions(Gainea game, int[] locations, NT_Action action, RequiredActionContainer container) {
        for (int i = 0; i < locations.length; i++) {
            Location location = game.state.getMap().getLocation(locations[i]);
            int optionIndex = i;
            MapAction mapAction = new MapAction(game, MapAction.TYPE_PLACE, location.getNumber(), () ->
                    container.reactionResult(action, optionIndex)
            );
            mapAction.setVisible(true);
            mapAction.setPosition(location.getCoordinates().getDisplayX(), location.getCoordinates().getDisplayY());
            container.showMapAction(mapAction);
        }
    }
}

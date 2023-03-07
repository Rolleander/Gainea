package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.ingame.unit.MenuUnit;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.map.Continent;
import com.broll.gainea.server.core.map.Island;
import com.broll.gainea.server.core.map.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UnitSelectionWindow {

    public static Table create(Gainea game, Skin skin, Location location, Collection<MapObjectRender> stack) {
        List<NT_Unit> units = stack.stream().sorted(Comparator.comparingInt(MapObjectRender::getRank)).
                map(MapObjectRender::getObject).filter(it -> it instanceof NT_Unit).map(it -> (NT_Unit) it).collect(Collectors.toList());
        Table window = new Table(skin);
        window.pad(10);
        window.setBackground("menu-bg");
        window.top();
        window.add(LabelUtils.label(skin, location.toString())).row();
        if (location instanceof Area) {
            AreaCollection container = location.getContainer();
            String name = container.getName();
            if (container instanceof Island) {
                name = "Insel: " + name;
            } else if (container instanceof Continent) {
                name = "Kontinent: " + name;
            }
            window.add(LabelUtils.info(skin, name)).padTop(0).padBottom(10).row();
        }
        window.row();
        int count = units.size();
        if (count > 1) {
            int power = units.stream().map(u -> (int) u.power).reduce(0, Integer::sum);
            int health = units.stream().map(u -> (int) u.health).reduce(0, Integer::sum);
            int maxHealth = units.stream().map(u -> (int) u.maxHealth).reduce(0, Integer::sum);
            window.add(new Label("Einheiten: " + count, skin)).left().padLeft(5).row();
            window.add(IconLabel.attack(game, power)).left().padLeft(5).row();
            window.add(IconLabel.health(game, health, maxHealth)).left().padLeft(5).row();
        }
        Table unitsTable = new Table(skin);
        List<MenuUnit> menuUnits = new ArrayList<>();
        ActionListener changed = () -> selected(game, menuUnits);
        Collections.reverse(units);
        units.forEach(unit -> {
            MenuUnit menuUnit = new MenuUnit(game, skin, unit, changed);
            menuUnit.setSelected(true);
            unitsTable.add(menuUnit).fillX().expandX().row();
            menuUnits.add(menuUnit);
        });
        ScrollPane scrollPane = new ScrollPane(unitsTable, skin);
        scrollPane.setScrollBarPositions(false, true);
        scrollPane.setOverscroll(false, false);
        scrollPane.setScrollingDisabled(true, false);
        scrollPane.setFadeScrollBars(false);
        window.add(scrollPane).fillX().expandX().row();
        if (count > 1) {
            Button select = new Button(skin);
            AtomicBoolean selectOrUnselect = new AtomicBoolean(false);
            select.add(new Label("Keins / Alle", skin));
            select.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    event.stop();
                    menuUnits.forEach(u -> u.setSelected(selectOrUnselect.get()));
                    selectOrUnselect.set(!selectOrUnselect.get());
                    selected(game, menuUnits);
                    return true;
                }
            });
            window.add(select).center();
        }
        selected(game, menuUnits);
        TableUtils.consumeClicks(window);
        return window;
    }

    private static void selected(Gainea game, List<MenuUnit> units) {
        List<NT_Unit> selected = units.stream().filter(MenuUnit::isSelected).map(MenuUnit::getUnit).collect(Collectors.toList());
        game.ui.inGameUI.selectedUnits(selected);
    }
}

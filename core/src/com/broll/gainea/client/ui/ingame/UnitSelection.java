package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.render.MapObjectRender;
import com.broll.gainea.client.ui.elements.ActionListener;
import com.broll.gainea.client.ui.elements.IconLabel;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.MenuUnit;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class UnitSelection {

    public static Table create(Gainea game, Skin skin, Location location, Collection<MapObjectRender> stack) {
        List<NT_Unit> units = stack.stream().map(MapObjectRender::getObject).filter(it -> it instanceof NT_Unit).map(it -> (NT_Unit) it).collect(Collectors.toList());
        Table window = new Table(skin);
        window.padTop(5);
        window.setBackground("menu-bg");
        window.top();
        window.add(LabelUtils.label(skin, location.toString())).padBottom(5).row();
        window.row();
        int count = units.size();
        if (count > 1) {
            int power = units.stream().map(u -> u.power).reduce(0, Integer::sum).intValue();
            int health = units.stream().map(u -> u.health).reduce(0, Integer::sum).intValue();
            int maxHealth = units.stream().map(u -> u.maxHealth).reduce(0, Integer::sum).intValue();
            window.add(new Label("Einheiten: " + count, skin)).left().padLeft(5).row();
            window.add(IconLabel.attack(game, skin, power)).left().padLeft(5).row();
            window.add(IconLabel.health(game, skin, health, maxHealth)).left().padLeft(5).row();
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
            AtomicBoolean selectOrUnselect = new AtomicBoolean(true);
            select.add(new Label("Keins / Alle", skin));
            select.addListener(new InputListener() {
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    menuUnits.forEach(u -> u.setSelected(selectOrUnselect.get()));
                    selectOrUnselect.set(!selectOrUnselect.get());
                    selected(game, menuUnits);
                    return true;
                }
            });
            window.add(select).center();
        }
        selected(game, menuUnits);
        return window;
    }

    private static void selected(Gainea game, List<MenuUnit> units) {
        List<NT_Unit> selected = units.stream().filter(MenuUnit::isSelected).map(MenuUnit::getUnit).collect(Collectors.toList());
        game.ui.getInGameUI().selectedUnits(selected);
    }
}

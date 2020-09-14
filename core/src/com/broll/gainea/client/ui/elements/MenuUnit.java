package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Scaling;
import com.broll.gainea.Gainea;
import com.broll.gainea.net.NT_Unit;
import com.esotericsoftware.minlog.Log;

import java.util.function.Consumer;

public class MenuUnit extends Table {

    private boolean selected = false;
    private NT_Unit unit;

    public MenuUnit(Gainea game, Skin skin, NT_Unit unit, ActionListener selectChanged) {
        this.unit = unit;
        setSkin(skin);
        setSelected(false);
        TextureRegion icon = IconUtils.unitIcon(game, unit.icon);
        Image image = new Image(icon);
        image.setScaling(Scaling.fill);
        image.setOrigin(30, 30);
        image.layout();
        add(image).size(60, 60).pad(3).center();
        left();
        Table table = new Table();
        table.left();
        table.add(new Label(unit.name, skin)).left().row();
        table.add(IconLabel.attack(game, skin, unit.power)).left().row();
        table.add(IconLabel.health(game, skin, unit.health, unit.maxHealth)).left().row();
        add(table).fillX().expandX().spaceLeft(10);
        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setSelected(!selected);
                selectChanged.action();
            }
        });
    }


    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setBackground("highlight");
        } else {
            setBackground("menu-bg");
        }
    }

    public boolean isSelected() {
        return selected;
    }

    public NT_Unit getUnit() {
        return unit;
    }
}

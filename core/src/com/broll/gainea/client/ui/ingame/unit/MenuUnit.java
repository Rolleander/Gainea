package com.broll.gainea.client.ui.ingame.unit;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Unit;

public class MenuUnit extends Table {

    private boolean selected = false;
    private NT_Unit unit;

    public MenuUnit(Gainea game, Skin skin, NT_Unit unit, ActionListener selectChanged) {
        this.unit = unit;
        setSkin(skin);
        setSelected(false);
        TextureRegion icon = TextureUtils.unitIcon(game, unit.icon);
        Image image = new Image(icon);
        image.setScaling(Scaling.fill);
        image.setOrigin(41, 41);
        image.layout();
        add(image).pad(3).center();
        left();
        Table table = new Table();
        table.left();
        table.add(LabelUtils.info(skin,unit.name)).left().spaceBottom(10).row();
        if (unit instanceof NT_Monster) {
            Table stars = new Table();
            for (int i = 0; i < ((NT_Monster) unit).stars; i++) {
                stars.add(new Image(TextureUtils.icon(game, 2))).left();
            }
            table.add(stars).left().row();
        }
        table.add(IconLabel.attack(game, skin, unit.power)).left().row();
        table.add(IconLabel.health(game, skin, unit.health, unit.maxHealth)).left().row();
        add(table).fillX().expandX().spaceLeft(10);
        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
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

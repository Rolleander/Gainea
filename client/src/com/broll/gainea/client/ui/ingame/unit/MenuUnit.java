package com.broll.gainea.client.ui.ingame.unit;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.utils.ActionListener;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.objects.monster.MonsterBehavior;

import org.apache.commons.lang3.StringUtils;

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
        table.add(LabelUtils.info(skin, unit.name)).left().spaceBottom(5).row();
        if (unit instanceof NT_Monster) {
            NT_Monster m = (NT_Monster) unit;
            Table stars = new Table();
            for (int i = 0; i < ((NT_Monster) unit).stars; i++) {
                stars.add(new Image(TextureUtils.icon(game, 2))).left();
            }
            if (m.actionTimer != -1) {
                String behavior = MonsterBehavior.values()[((NT_Monster) unit).behavior].getLabel();
                stars.add(LabelUtils.markup(skin, behavior)).spaceLeft(10);
            }
            table.add(stars).left().spaceBottom(5).row();
        }
        Table row = new Table(skin);
        row.add(IconLabel.attack(game, unit.power)).left();
        row.add(IconLabel.health(game, unit.health, unit.maxHealth)).spaceLeft(20).left();
        row.add(new IconLabel(game, 8, "" + unit.kills)).spaceLeft(20).left();
        table.add(row).left().row();
        if (StringUtils.isNotEmpty(unit.description)) {
            Label info = LabelUtils.markup(skin, "[DARK_GRAY]" + unit.description);
            table.add(LabelUtils.autoWrap(info, 180)).left().expandX().fillX().row();
        }
        add(table).fillX().expandX().minWidth(180).spaceLeft(10);
        setTouchable(Touchable.enabled);
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                event.stop();
                setSelected(!selected);
                selectChanged.action();
            }
        });
        this.padRight(8);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        if (selected) {
            setBackground("highlight");
        } else {
            setBackground("menu-bg");
        }
    }

    public NT_Unit getUnit() {
        return unit;
    }
}

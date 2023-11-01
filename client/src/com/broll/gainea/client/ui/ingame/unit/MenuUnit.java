package com.broll.gainea.client.ui.ingame.unit;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.objects.monster.MonsterBehavior;

import org.apache.commons.lang3.StringUtils;

public class MenuUnit extends Table {

    private NT_BoardObject object;

    public MenuUnit(Gainea game, Skin skin, NT_BoardObject object) {
        this.object = object;
        setSkin(skin);
        MapObjectRender render = MapObjectRender.createRender(game, game.ui.skin, object);
        render.showDescription = false;
        if (render instanceof UnitRender) {
            ((UnitRender) render).setStackTop(false);
        }
        render.setSize(90, 90);
        add(render).pad(3).center();
        left();
        Table table = new Table();
        table.left();
        table.add(LabelUtils.info(skin, this.object.name)).left().spaceBottom(5).row();
        if (this.object instanceof NT_Monster) {
            NT_Monster m = (NT_Monster) this.object;
            Table stars = new Table();
            for (int i = 0; i < m.stars; i++) {
                stars.add(new Image(TextureUtils.icon(game, 2))).left();
            }
            if (m.actionTimer != -1) {
                String behavior = MonsterBehavior.values()[((NT_Monster) this.object).behavior].getLabel();
                stars.add(LabelUtils.markup(skin, behavior)).spaceLeft(10);
            }
            table.add(stars).left().spaceBottom(5).row();
        }

        if (this.object instanceof NT_Unit) {
            NT_Unit unit = (NT_Unit) object;
            Table row = new Table(skin);
            row.defaults().spaceLeft(20).left();
            row.add(IconLabel.attack(game, unit.power));
            row.add(IconLabel.health(game, unit.health, unit.maxHealth));
            row.add(new IconLabel(game, 8, String.valueOf(unit.kills)));
            table.add(row).left().row();
        }

        if (StringUtils.isNotEmpty(this.object.description)) {
            Label info = LabelUtils.markup(skin, "[DARK_GRAY]" + this.object.description);
            table.add(LabelUtils.autoWrap(info, 240)).left().expandX().fillX().row();
        }
        add(table).fillX().expandX().minWidth(240).spaceLeft(10);
        setTouchable(Touchable.enabled);
        TableUtils.consumeClicks(this);
        this.padRight(8);
    }

    public NT_BoardObject getObject() {
        return object;
    }
}

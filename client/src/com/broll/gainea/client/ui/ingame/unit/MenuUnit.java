package com.broll.gainea.client.ui.ingame.unit;

import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.BoardObjectState;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.ingame.map.MapObjectRender;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.core.objects.monster.MonsterBehavior;

public class MenuUnit extends Table {

    private BoardObjectState object;

    public MenuUnit(Gainea game, BoardObjectState object) {
        this(game, object, false);
    }

    public MenuUnit(Gainea game, BoardObjectState object, boolean hideKills) {
        this.object = object;
        setSkin(game.ui.skin);
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
        table.add(LabelUtils.info(getSkin(), this.object.name())).left().spaceBottom(5).row();
        if (this.object.isMonster()) {
            Table stars = new Table();
            for (int i = 0; i < object.stars(); i++) {
                stars.add(new Image(TextureUtils.icon(game, 2))).left();
            }
            if (object.actionTimer() != -1) {
                String behavior = MonsterBehavior.values()[object.behavior()].getLabel();
                stars.add(LabelUtils.markup(getSkin(), behavior)).spaceLeft(10);
            }
            table.add(stars).left().spaceBottom(5).row();
        }

        if (this.object.isUnit()) {
            Table row = new Table(getSkin());
            row.defaults().spaceLeft(20).left();
            row.add(IconLabel.attack(game, object.power()));
            row.add(IconLabel.health(game, object.health(), object.maxHealth()));
            if (!hideKills) {
                row.add(new IconLabel(game, 8, String.valueOf(object.kills())));
            }
            table.add(row).left().row();
        }

        if (!this.object.description().isEmpty()) {
            Label info = LabelUtils.markup(getSkin(), "[DARK_GRAY]" + this.object.description());
            table.add(LabelUtils.autoWrap(info, 240)).left().expandX().fillX().row();
        }
        add(table).fillX().expandX().minWidth(240).spaceLeft(10);
        setTouchable(Touchable.enabled);
        TableUtils.consumeClicks(this);
        this.padRight(8);
    }

    public BoardObjectState getObject() {
        return object;
    }
}

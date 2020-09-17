package com.broll.gainea.client.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.elements.ClosableWindow;
import com.broll.gainea.client.ui.elements.IconLabel;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.client.ui.elements.MenuUnit;
import com.broll.gainea.client.ui.elements.TableUtils;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionFactory;
import com.broll.gainea.server.core.objects.BattleObject;

import java.util.List;

public class FractionWindow extends ClosableWindow {
    public FractionWindow(Gainea game, Skin skin) {
        super(game, "Fraktionen", skin);
        TableUtils.consumeClicks(this);
        Table list = new Table(skin);
        add(list).left().top().space(10).padTop(5);
        Table content = new Table(skin);
        add(content).top().expand().fill().space(10);
        List<Fraction> fractions = FractionFactory.createAll();
        fractions.sort((f1, f2) -> f1.getType().compareTo(f2.getType()));
        fractions.forEach(fraction -> {
            Table button = new Table(skin);
            button.add(LabelUtils.info(skin, fraction.getType().getName()));
            list.add(button).left().spaceBottom(5).row();
            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    content.clear();
                    content.add(fraction(fraction)).expand().fill().left().top();
                }
            });
        });
        center(650, 500);
    }

    private Table fraction(Fraction fraction) {
        Table table = new Table(skin);
        table.setBackground("menu-bg");
        table.top();
        table.left();
        table.pad(5);
        table.padLeft(20);
        table.add(LabelUtils.title(skin, fraction.getType().getName())).left().row();
        Table t = new Table();
        t.add(LabelUtils.info(skin, "Kommandant")).left();
        t.add(unit(fraction.createCommander(null))).left().spaceLeft(50).row();
        t.add(LabelUtils.info(skin, "Soldaten")).left();
        t.add(unit(fraction.createSoldier(null))).left().spaceLeft(50).row();
        table.add(t).left().row();
        FractionDescription description = fraction.getDescription();
        Label info = LabelUtils.info(skin, description.getGeneral());
        int w = 450;
        info.setWrap(true);
        info.setWidth(w);
        info.pack();
        table.add(info).left().spaceTop(20).padBottom(20).width(w).row();
        description.getPlus().forEach(text -> table.add(new IconLabel(game, skin, 4, text)).left().spaceTop(10).row());
        description.getContra().forEach(text -> table.add(new IconLabel(game, skin, 5, text)).left().spaceTop(10).row());
        return table;
    }

    private MenuUnit unit(BattleObject object) {
        MenuUnit unit = new MenuUnit(game, skin, object.nt(), () -> {
        });
        unit.setTouchable(Touchable.disabled);
        return unit;
    }
}

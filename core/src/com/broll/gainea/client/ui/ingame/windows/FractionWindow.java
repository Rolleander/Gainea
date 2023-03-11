package com.broll.gainea.client.ui.ingame.windows;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.components.IconLabel;
import com.broll.gainea.client.ui.ingame.unit.MenuUnit;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TableUtils;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.objects.Unit;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class FractionWindow extends MenuWindow {
    public FractionWindow(Gainea game) {
        super(game, "Fraktionen", game.ui.skin);
        TableUtils.consumeClicks(this);
        Table list = new Table(skin);
        add(list).left().top().space(10).padTop(5);
        Table content = new Table(skin);
        add(content).top().expand().fill().space(10);
        List<Fraction> fractions = Arrays.stream(FractionType.values()).map(FractionType::create).collect(Collectors.toList());
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
        center(950, 600);
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
        t.add(unit(fraction.createCommander())).left().spaceLeft(50).row();
        t.add(LabelUtils.info(skin, "Soldaten")).left();
        t.add(unit(fraction.createSoldier())).left().spaceLeft(50).row();
        table.add(t).left().row();
        FractionDescription description = fraction.getDescription();
        int w = 450;
        table.add(LabelUtils.autoWrap(LabelUtils.info(skin, description.getGeneral()), w)).left().spaceTop(20).padBottom(20).width(w).row();
        description.getPlus().forEach(text -> table.add(new IconLabel(game, 4, text)).left().spaceTop(10).row());
        description.getContra().forEach(text -> table.add(new IconLabel(game, 5, text)).left().spaceTop(10).row());
        return table;
    }

    private MenuUnit unit(Unit object) {
        MenuUnit unit = new MenuUnit(game, skin, object.nt(), () -> {
        });
        unit.setTouchable(Touchable.disabled);
        return unit;
    }

    @Override
    public void update() {

    }
}

package com.broll.gainea.client.ui.ingame.windows.lib;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.unit.MenuUnit;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.client.ui.utils.TextureUtils;
import com.broll.gainea.net.NT_Lib_Fraction;

import java.util.Arrays;

public class FractionTable extends Table {
    private final Gainea game;

    public FractionTable(Gainea game, NT_Lib_Fraction fraction) {
        super(game.ui.skin);
        this.game = game;
        setBackground("menu-bg");
        center();
        top();
        pad(5);
        padLeft(20);
        add(LabelUtils.title(getSkin(), fraction.name)).left().row();
        Table t = new Table();
        t.add(LabelUtils.info(getSkin(), "Kommandant")).left();
        t.add(new MenuUnit(game, fraction.commander, true)).left().spaceLeft(50).spaceBottom(10).row();
        t.add(LabelUtils.info(getSkin(), "Soldaten")).left();
        t.add(new MenuUnit(game, fraction.soldier, true)).left().spaceLeft(50).row();
        add(t).left().row();
        int w = 450;
        add(LabelUtils.autoWrap(LabelUtils.info(getSkin(), fraction.desc), w)).left().spaceTop(20).padBottom(20).width(w).row();
        Arrays.stream(fraction.descPositive).forEach(text -> add(info(4, text)).left().spaceTop(10).row());
        Arrays.stream(fraction.descNegative).forEach(text -> add(info(5, text)).left().spaceTop(10).row());
    }

    private Table info(int icon, String text) {
        Table table = new Table();
        table.add(new Image(TextureUtils.icon(game, icon))).center().spaceRight(15);
        Label label = LabelUtils.info(getSkin(), text);
        int w = 700;
        table.add(LabelUtils.autoWrap(label, w)).width(w);
        return table;
    }

}

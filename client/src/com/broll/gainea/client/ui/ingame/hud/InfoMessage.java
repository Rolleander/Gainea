package com.broll.gainea.client.ui.ingame.hud;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.utils.LabelUtils;

public class InfoMessage extends Table {

    public int index;

    public InfoMessage(Gainea game, String text, Color color) {
        super(game.ui.skin);
        init(text, color);
    }

    public InfoMessage(Gainea game, String text) {
        super(game.ui.skin);
        init(text, Color.BLACK);
    }

    public InfoMessage(Gainea game, Table content) {
        super(game.ui.skin);
        setBackground("info-msg");
        pad(10);
        add(content);
    }
    
    public InfoMessage(Gainea game, String text, Table content, Color color) {
        super(game.ui.skin);
        add(content).spaceRight(20);
        init(text, color);
    }

    private void init(String text, Color color) {
        setBackground("info-msg");
        pad(10);
        Label label = LabelUtils.label(getSkin(), text);
        add(LabelUtils.color(label, color));
    }

}

package com.broll.gainea.client.ui.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelUtils {

    public static Label color(Label label, Color color) {
        label.setStyle(new Label.LabelStyle(label.getStyle().font, color));
        return label;
    }

    public static Label info(Skin skin, String text) {
        return new Label(text, skin);
    }

    public static Label markup(Skin skin, String text) {
        return new Label("[BLACK]"+text, skin, "markup");
    }

    public static Label title(Skin skin, String text) {
        Label l = new Label(text, skin, "title");
        return l;
    }

    public static Label autoWrap(Label label, int width){
        label.setWrap(true);
        label.setWidth(width);
        label.pack();
        return label;
    }

    public static Label label(Skin skin, String text) {
        Label l = new Label(text, skin, "title");
        l.setFontScale(0.7f);
        return l;
    }

}

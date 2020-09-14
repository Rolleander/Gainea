package com.broll.gainea.client.ui.elements;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class LabelUtils {

    public static Label info(Skin skin, String text) {
        return new Label(filter(text), skin);
    }


    public static Label title(Skin skin, String text) {
        Label l = new Label(filter(text), skin, "title");
        return l;
    }

    public static Label label(Skin skin, String text) {
        Label l = new Label(filter(text), skin, "title");
        l.setFontScale(0.7f);
        return l;
    }

    public static String filter(String text) {
        text = text.replaceAll("ä", "ae");
        text = text.replaceAll("ü", "ue");
        text = text.replaceAll("ö", "oe");
        text = text.replaceAll("Ä", "Äe");
        text = text.replaceAll("Ü", "Üe");
        text = text.replaceAll("Ö", "Öe");
        text = text.replaceAll("ß", "ss");
        return text;
    }

}

package com.broll.gainea.client.ui.utils;

import com.badlogic.gdx.graphics.Color;

public class ColorUtils {

    public static Color darker(Color c, float by) {
        return c.cpy().mul(1 - by, 1 - by, 1 - by, 1);
    }
}

package com.broll.gainea.misc;

public class EnumUtils {

    public static boolean inBounds(int d, Class<? extends Enum> eClass) {
        return d >= 0 && d < eClass.getEnumConstants().length;
    }
}

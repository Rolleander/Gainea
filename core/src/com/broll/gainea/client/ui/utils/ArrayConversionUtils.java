package com.broll.gainea.client.ui.utils;

public class ArrayConversionUtils {

    public static short[] toShort(int[] ints){
        short[] shorts =new short[ints.length];
        for(int i=0; i< shorts.length; i++){
            shorts[i] = (short) ints[i];
        }
        return shorts;
    }

    public static int[] toInt(short[] shorts){
        int[] ints =new int[shorts.length];
        for(int i=0; i< ints.length; i++){
            ints[i] = shorts[i];
        }
        return ints;
    }
}

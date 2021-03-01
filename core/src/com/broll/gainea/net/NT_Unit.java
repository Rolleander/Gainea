package com.broll.gainea.net;

public class NT_Unit extends NT_BoardObject {

    public final static int TYPE_MALE = 0;
    public final static int TYPE_FEMALE = 1;
    public final static int NO_OWNER = -1;
    public int health,maxHealth,power;
    public int owner = NO_OWNER;
    public int type = TYPE_MALE;
}

package com.broll.gainea.net;

public class NT_Unit extends NT_BoardObject {

    public final static byte TYPE_MALE = 0;
    public final static byte TYPE_FEMALE = 1;
    public final static byte NO_OWNER = -1;
    public short health,maxHealth,power;
    public short owner = NO_OWNER;
    public byte type = TYPE_MALE;
}

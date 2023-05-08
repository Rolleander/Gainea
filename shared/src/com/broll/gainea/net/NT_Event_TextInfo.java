package com.broll.gainea.net;

public class NT_Event_TextInfo extends NT_Event {
    public final static int TYPE_MESSAGE_LOG = 0;
    public final static int TYPE_MESSAGE_DISPLAY = 1;
    public final static int TYPE_CONFIRM_MESSAGE = 2;
    public String text;
    public int type;
}

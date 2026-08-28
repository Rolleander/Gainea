package com.broll.gainea.net;

public class NT_Battle_Update {

    public final static int STATE_FIGHTING = 0;
    public final static int STATE_ATTACKER_WON = 1;
    public final static int STATE_DEFENDER_WON = 2;
    public final static int STATE_DRAW = 3;

    public NT_Battle_Roll[] attackerRolls;
    public NT_Battle_Roll[] defenderRolls;
    public NT_Battle_Damage[] damage;
    public int state;
}

package com.broll.gainea.net;

public class NT_Battle_Update {

    public final static int STATE_FIGHTING=0;
    public final static int STATE_ATTACKER_WON=1;
    public final static int STATE_DEFENDER_WON=2;

    public int[] attackerRolls;
    public int[] defenderRolls;
    public NT_Unit[] attackers;
    public NT_Unit[] defenders;
    public int[] damageToAttackers;
    public int[] damageToDefenders;
    public int state;
}

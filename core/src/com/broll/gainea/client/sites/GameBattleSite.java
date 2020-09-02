package com.broll.gainea.client.sites;

import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.networklib.PackageReceiver;

import java.util.Arrays;
import java.util.List;

public class GameBattleSite extends AbstractGameSite {


    @PackageReceiver
    public void received(NT_Battle_Start battle) {
        List<NT_Unit> attackers = Arrays.asList(battle.attackers);
        List<NT_Unit> defenders = Arrays.asList(battle.defenders);
        int location = defenders.get(0).location;

    }

    @PackageReceiver
    public void received(NT_Battle_Update battle) {
        int[] attackRolls = battle.attackerRolls;
        int[] defenderRolls = battle.defenderRolls;
        List<NT_Unit> killedAttackers = Arrays.asList(battle.killedAttacker);
        List<NT_Unit> killedDefenders = Arrays.asList(battle.killedDefender);
        List<NT_Unit> remainingAttackers = Arrays.asList(battle.remainingAttackers);
        List<NT_Unit> remainingDefenders = Arrays.asList(battle.remainingDefenders);
        switch (battle.state) {
            case NT_Battle_Update.STATE_ATTACKER_WON:
                break;
            case NT_Battle_Update.STATE_DEFENDER_WON:
                break;
            case NT_Battle_Update.STATE_FIGHTING:
                break;
        }
    }

    private void battleEnded() {
    }
}

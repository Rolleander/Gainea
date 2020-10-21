package com.broll.gainea.client.game.sites;

import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.networklib.PackageReceiver;
import com.esotericsoftware.minlog.Log;
import com.google.common.collect.Lists;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class GameBattleSite extends AbstractGameSite {

    private List<NT_Unit> attackers;
    private List<NT_Unit> defenders;

    @PackageReceiver
    public void received(NT_Battle_Start battle) {
        game.state.updateIdleState(false);
        this.attackers = Lists.newArrayList(battle.attackers);
        this.defenders = Lists.newArrayList(battle.defenders);
        int location = defenders.get(0).location;
        Log.info("Start fight: Attackers (" + attackers.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ") Defenders (" + defenders.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ")");
        game.ui.inGameUI.startBattle(attackers, defenders, game.state.getMap().getLocation(location));
    }


    private Stack<NT_Unit> calcDamageUpdates(List<NT_Unit> before, List<NT_Unit> after) {
        Stack<NT_Unit> damages = new Stack<>();
        for (int i = 0; i < after.size(); i++) {
            int delta = before.get(i).health - after.get(i).health;
            for (int d = 0; d < delta; d++) {
                damages.push(after.get(i));
            }
        }
        return damages;
    }

    @PackageReceiver
    public void received(NT_Battle_Update battle) {
        int[] attackRolls = battle.attackerRolls;
        int[] defenderRolls = battle.defenderRolls;
        List<NT_Unit> attackers = Lists.newArrayList(battle.attackers);
        List<NT_Unit> defenders = Lists.newArrayList(battle.defenders);
        Log.info("Update fight: Attackers (" + attackers.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ") Defenders (" + defenders.stream().map(it -> it.id + "| " + it.name + " " + it.power + " " + it.health).collect(Collectors.joining(", ")) + ")");
        Stack<NT_Unit> damagedAttackers = calcDamageUpdates(this.attackers, attackers);
        Stack<NT_Unit> damagedDefenders = calcDamageUpdates(this.defenders, defenders);
        this.attackers = attackers;
        this.defenders = defenders;
        game.ui.inGameUI.updateBattle(attackRolls, defenderRolls, damagedAttackers, damagedDefenders, battle.state);
    }
}

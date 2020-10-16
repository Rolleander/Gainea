package com.broll.gainea.client.game.sites;

import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_Unit;
import com.broll.networklib.PackageReceiver;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameBattleSite extends AbstractGameSite {

    private List<NT_Unit> attackers;
    private List<NT_Unit> defenders;

    @PackageReceiver
    public void received(NT_Battle_Start battle) {
        this.attackers = Arrays.asList(battle.attackers);
        this.defenders = Arrays.asList(battle.defenders);
        int location = defenders.get(0).location;
        game.ui.inGameUI.startBattle(attackers, defenders,  game.state.getMap().getLocation(location));
    }

    private List<Pair<NT_Unit, Integer>> doDamageUpdates(List<NT_Unit> before, List<NT_Unit> after) {
        List<Pair<NT_Unit, Integer>> updates = new ArrayList<>();
        for (int i = 0; i < after.size(); i++) {
            int delta = before.get(i).health - after.get(i).health;
            if (after.get(i).health <= 0) {
                before.remove(i);
            }
            if (delta != 0) {
                updates.add(Pair.of(after.get(i), delta));
            }
        }
        return updates;
    }

    @PackageReceiver
    public void received(NT_Battle_Update battle) {
        int[] attackRolls = battle.attackerRolls;
        int[] defenderRolls = battle.defenderRolls;
        List<NT_Unit> attackers = Arrays.asList(battle.attackers);
        List<NT_Unit> defenders = Arrays.asList(battle.defenders);
        List<Pair<NT_Unit, Integer>> damagedAttackers = doDamageUpdates(this.attackers, attackers);
        List<Pair<NT_Unit, Integer>> damagedDefenders = doDamageUpdates(this.defenders, defenders);
        game.ui.inGameUI.updateBattle(attackRolls,defenderRolls,damagedAttackers,damagedDefenders,battle.state);
    }
}

package com.broll.gainea.server.core.objects;

import com.broll.gainea.server.core.battle.BattleResult;

public class GodDragon extends Monster {

    public GodDragon() {
        setIcon(58);
        setName("Götterdrache");
        setStats(8, 8);
        setBehavior(MonsterBehavior.AGGRESSIVE);
        setActivity(MonsterActivity.ALWAYS);
    }

    @Override
    public void onDeath(BattleResult throughBattle) {
        if (throughBattle != null && throughBattle.getWinnerPlayer() != null) {
            throughBattle.getWinnerPlayer().getGoalHandler().addPoints(1);
        }
    }
}

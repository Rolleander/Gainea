package com.broll.gainea.server.core.objects.monster;

import com.broll.gainea.server.core.battle.BattleResult;

public class GodDragon extends Monster {

    public GodDragon() {
        setIcon(58);
        setName("Götterdrache");
        setStats(8, 8);
        setBehavior(MonsterBehavior.AGGRESSIVE);
        setActivity(MonsterActivity.ALWAYS);
        setMotion(MonsterMotion.AIRBORNE);
    }

    @Override
    public void onDeath(BattleResult throughBattle) {
        if (throughBattle != null) {
            throughBattle.getKillingPlayers(this).forEach(p -> p.getGoalHandler().addPoints(1));
        }
    }
}

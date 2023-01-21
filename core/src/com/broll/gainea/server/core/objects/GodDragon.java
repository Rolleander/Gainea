package com.broll.gainea.server.core.objects;

public class GodDragon extends Monster {

    public GodDragon() {
        setIcon(58);
        setName("Götterdrache");
        setStats(8,8);
        setBehavior(MonsterBehavior.AGGRESSIVE);
        setActivity(MonsterActivity.ALWAYS);
    }

}

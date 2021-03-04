package com.broll.gainea.server.core.objects;

import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Unit;

public class Monster extends BattleObject {

    public Monster() {
        super(null);
    }

    public int getStars() {
        return getStars(this);
    }

    @Override
    public NT_Monster nt() {
        NT_Monster monster = new NT_Monster();
        monster.stars = (byte) getStars();
        fillBattleObject(monster);
        return monster;
    }

    public static int getStars(Monster monster) {
        return stars(monster.getPower().getRootValue(), monster.getMaxHealth().getRootValue());
    }

    public static int stars(int power, int health) {
        return (power + health) / 2;
    }
}

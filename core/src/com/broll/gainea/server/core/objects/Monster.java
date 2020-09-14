package com.broll.gainea.server.core.objects;

public class Monster extends BattleObject {

    public Monster() {
        super(null);
    }


    public int getStars() {
        return getStars(this);
    }

    public static int getStars(Monster monster) {
        return stars(monster.getPower(), monster.getMaxHealth());
    }

    public static int stars(int power, int health) {
        return (power + health) / 2;
    }
}

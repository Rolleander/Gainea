package com.broll.gainea.server.core.objects;

import com.broll.gainea.net.NT_Monster;

public class Monster extends BattleObject {

    private MonsterBehavior behavior = MonsterBehavior.RESIDENT;
    private MonsterActivity activity = MonsterActivity.SOMETIMES;
    private int actionTimer = NT_Monster.NO_ACTION_TIMER;

    public Monster() {
        super(null);
    }

    public int getStars() {
        return getStars(this);
    }

    public void setBehavior(MonsterBehavior behavior) {
        this.behavior = behavior;
    }

    public void setActivity(MonsterActivity activity) {
        this.activity = activity;
        resetActionTimer();
    }

    private void resetActionTimer() {
        if (behavior != MonsterBehavior.RESIDENT) {
            this.actionTimer = activity.getTurnTimer();
        }
    }

    @Override
    public NT_Monster nt() {
        NT_Monster monster = new NT_Monster();
        monster.stars = (byte) getStars();
        monster.actionTimer = (byte) actionTimer;
        monster.behavior = (byte) behavior.ordinal();
        fillBattleObject(monster);
        return monster;
    }

    @Override
    public void roundStarted() {
        if (actionTimer != NT_Monster.NO_ACTION_TIMER && owner == null && isAlive()) {
            actionTimer--;
            if (actionTimer == 0) {
                this.behavior.doAction(game, this);
                resetActionTimer();
            }
        }
    }

    public static int getStars(Monster monster) {
        return stars(monster.getPower().getRootValue(), monster.getMaxHealth().getRootValue());
    }

    public static int stars(int power, int health) {
        return (power + health) / 2;
    }


}

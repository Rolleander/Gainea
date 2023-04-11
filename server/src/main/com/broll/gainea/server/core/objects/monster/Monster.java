package com.broll.gainea.server.core.objects.monster;

import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;

public class Monster extends Unit {

    private final static int MAX_STARS = 8;
    private MonsterBehavior behavior = MonsterBehavior.RESIDENT;
    private MonsterActivity activity = MonsterActivity.SOMETIMES;
    private MonsterMotion motion = MonsterMotion.TERRESTRIAL;
    private int actionTimer = NT_Monster.NO_ACTION_TIMER;

    public Monster() {
        super(null);
    }

    public void removeActionTimer() {
        this.actionTimer = NT_Monster.NO_ACTION_TIMER;
    }

    public int getStars() {
        return getStars(this);
    }

    public void setBehavior(MonsterBehavior behavior) {
        this.behavior = behavior;
    }

    public void setMotion(MonsterMotion motion) {
        this.motion = motion;
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
        if (actionTimer != NT_Monster.NO_ACTION_TIMER && isAlive() && isBehaviorActive() && game.getRounds() > 1) {
            progressBehavior();
        }
    }

    @Override
    public boolean canMoveTo(Location to) {
        boolean canMove = motion.canMoveTo(getLocation(), to);
        if (!canMove && owner != null) {
            //allow controlled monsters to walk with the players army
            return super.canMoveTo(to);
        }
        return canMove;
    }

    public boolean mightAttackSoon() {
        return actionTimer == 1 && behavior != MonsterBehavior.RESIDENT && behavior != MonsterBehavior.FRIENDLY;
    }

    protected boolean isBehaviorActive() {
        if (owner == null) {
            return true;
        }
        return !controllable;
    }

    private void progressBehavior() {
        actionTimer--;
        if (actionTimer == 0) {
            this.behavior.doAction(game, this);
            resetActionTimer();
        }
    }

    public static int getStars(Monster monster) {
        return stars(monster.getPower().getRootValue(), monster.getMaxHealth().getRootValue());
    }

    public static int stars(int power, int health) {
        return Math.min((power + health) / 2, MAX_STARS);
    }

}

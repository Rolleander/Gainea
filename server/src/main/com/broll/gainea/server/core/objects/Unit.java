package com.broll.gainea.server.core.objects;

import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.objects.buffs.BuffableInt;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.player.Player;

public abstract class Unit extends MapObject {

    private BuffableInt<MapObject> maxHealth = new BuffableInt<>(this, 0);
    private BuffableInt<MapObject> power = new BuffableInt<>(this, 0);
    private BuffableInt<MapObject> health = new BuffableInt<>(this, 0);
    private BuffableInt<MapObject> attacksPerTurn = new BuffableInt<>(this, 1); //default 1 attack

    private BuffableInt<MapObject> numberPlus = new BuffableInt<>(this, 0);

    private int attackCount;
    private boolean moveOrAttackRestriction = true; //usually units can only attack or move in one turn
    private boolean attacked = false;
    private boolean moved = false;
    protected boolean controllable = true;
    private int type = NT_Unit.TYPE_MALE;
    private int kills;
    private boolean justSpawned = true;

    public Unit(Player owner) {
        super(owner);
        health.setMinValue(0);
        maxHealth.setMinValue(1);
        power.setMinValue(0);
    }

    public static void copy(Unit from, Unit to) {
        to.maxHealth = from.maxHealth.copy(to);
        to.power = from.power.copy(to);
        to.health = from.health.copy(to);
        to.owner = from.owner;
        to.attackCount = from.attackCount;
        to.moveCount = from.moveCount;
        to.attacksPerTurn = from.attacksPerTurn.copy(to);
        to.movesPerTurn = from.movesPerTurn.copy(to);
        to.numberPlus = from.numberPlus.copy(to);
        to.setMoveOrAttackRestriction(from.isMoveOrAttackRestriction());
        to.setIcon(from.getIcon());
        to.setLocation(from.getLocation());
        to.setName(from.getName());
        to.setScale(from.getScale());
    }

    @Override
    public void turnStart() {
        super.turnStart();
        justSpawned = false;
        attackCount = 0;
        moved = false;
        attacked = false;
    }

    @Override
    public boolean hasRemainingMove() {
        if (justSpawned) return false;
        if (moveOrAttackRestriction && attacked) {
            return false;
        }
        return super.hasRemainingMove();
    }

    public boolean hasRemainingAttack() {
        if (justSpawned) return false;
        if (moveOrAttackRestriction && moved) {
            return false;
        }
        return attackCount < attacksPerTurn.getValue();
    }

    public void onDeath(BattleResult throughBattle) {

    }

    public FightingPower calcFightingPower(BattleContext context) {
        return new FightingPower(this).changeNumberPlus(numberPlus.getValue());
    }

    @Override
    public void moved() {
        super.moved();
        moved = true;
    }

    public void attacked() {
        attackCount++;
        attacked = true;
    }

    public void setStats(int power, int health) {
        setPower(power);
        setHealth(health);
    }

    public boolean isMoveOrAttackRestriction() {
        return moveOrAttackRestriction;
    }

    public void setMoveOrAttackRestriction(boolean moveOrAttackRestriction) {
        this.moveOrAttackRestriction = moveOrAttackRestriction;
    }

    @Override
    public BuffableInt<MapObject> getMovesPerTurn() {
        return super.getMovesPerTurn();
    }

    public Player getOwner() {
        return owner;
    }

    public boolean takeDamage() {
        return takeDamage(1);
    }

    public boolean takeDamage(int damage) {
        boolean aliveBefore = isAlive();
        health.addValue(-damage);
        return aliveBefore && isDead();
    }

    public void heal(int heal) {
        health.addValue(heal);
        if (health.getValue() > maxHealth.getValue()) {
            heal();
        }
    }

    public boolean isDead() {
        return health.getValue() <= 0;
    }

    public boolean isAlive() {
        return !isDead();
    }

    public void heal() {
        health.setValue(maxHealth.getRootValue());
    }

    public void setHealth(int health) {
        this.health.setValue(health);
        this.maxHealth.setValue(health);
    }

    public void changeHealth(int change) {
        this.health.addValue(change);
        this.maxHealth.addValue(change);
    }

    public void setPower(int power) {
        this.power.setValue(power);
    }

    public BuffableInt<MapObject> getPower() {
        return power;
    }

    public void addHealthBuff(IntBuff buff) {
        health.addBuff(buff);
        maxHealth.addBuff(buff);
    }

    public void clearBuffs() {
        power.clearBuffs();
        health.clearBuffs();
        maxHealth.clearBuffs();
        attacksPerTurn.clearBuffs();
        movesPerTurn.clearBuffs();
    }

    public BuffableInt<MapObject> getHealth() {
        return health;
    }

    public BuffableInt<MapObject> getMaxHealth() {
        return maxHealth;
    }

    public BuffableInt<MapObject> getAttacksPerTurn() {
        return attacksPerTurn;
    }

    public BuffableInt<MapObject> getNumberPlus() {
        return numberPlus;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public int getBattleStrength() {
        return getPower().getValue() + getHealth().getValue();
    }

    @Override
    public NT_Unit nt() {
        NT_Unit unit = new NT_Unit();
        fillBattleObject(unit);
        return unit;
    }

    protected void fillBattleObject(NT_Unit unit) {
        fillObject(unit);
        unit.health = health.getValue().shortValue();
        unit.maxHealth = maxHealth.getValue().shortValue();
        unit.power = power.getValue().shortValue();
        unit.type = (byte) type;
        unit.kills = (short) kills;
        if (owner != null) {
            unit.owner = (short) owner.getServerPlayer().getId();
        }
    }

    public boolean isHurt() {
        return health.getValue() < maxHealth.getValue();
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getKills() {
        return kills;
    }

    public void addKill() {
        this.kills++;
    }

    public boolean isControllable() {
        return controllable;
    }

    @Override
    public String toString() {
        return "Unit{" +
                "id=" + getId() +
                ", name='" + getName() +
                ", " + getPower() + "/" + getHealth() +
                ", location=" + getLocation() + '\'' +
                '}';
    }
}

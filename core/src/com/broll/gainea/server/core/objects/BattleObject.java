package com.broll.gainea.server.core.objects;

import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.player.Player;

public abstract class BattleObject extends MapObject  {

    private int maxHealth;
    private int power, health;
    private Player owner;
    protected boolean rooted = false;

    public BattleObject(Player owner) {
        this.owner = owner;
    }

    public void setStats(int power, int health) {
        this.power = power;
        this.maxHealth = health;
        this.health = health;
    }

    protected void onDeath() {

    }

    public boolean isRooted() {
        return rooted;
    }

    public Player getOwner() {
        return owner;
    }

    public void takeDamage() {
        takeDamage(1);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (isDead()) {
            onDeath();
        }
    }

    public void heal(int heal) {
        health += heal;
        if (health > maxHealth) {
            health = maxHealth;
        }
    }

    public boolean isDead() {
        return health <= 0;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void heal() {
        health = maxHealth;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getHealth() {
        return health;
    }

    public int getPower() {
        return power;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    @Override
    public NT_Unit nt() {
        NT_Unit unit = new NT_Unit();
        fillBattleObject(unit);
        return unit;
    }

    protected void fillBattleObject(NT_Unit unit) {
        fillObject(unit);
        unit.health = health;
        unit.maxHealth = maxHealth;
        unit.power = power;
        if (owner != null) {
            unit.owner = owner.getServerPlayer().getId();
        }
    }

}

package com.broll.gainea.server.core.objects;

import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.player.Player;

public abstract class BattleObject extends MapObject {

    private int maxHealth;
    private int power, health;
    private Player owner;

    public BattleObject(Player owner) {
        this.owner = owner;
    }

    public Player getOwner() {
        return owner;
    }

    public void takeDamage() {
        health--;
    }

    public boolean isDead() {
        return health <= 0;
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

    @Override
    public NT_Unit nt() {
        NT_Unit unit = new NT_Unit();
        fillObject(unit);
        unit.health = health;
        unit.maxHealth = maxHealth;
        unit.power = power;
        if(owner!=null){
            unit.owner = owner.getServerPlayer().getId();
        }
        return unit;
    }

}

package com.broll.gainea.server.core.fractions;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.actions.impl.PlaceUnitAction;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Fraction {

    protected int soldierHealth = 1;
    protected int soldierPower = 1;
    protected int commanderHealth = 3;
    protected int commanderPower = 3;

    private FractionType type;
    private FractionDescription description;
    protected GameContainer game;
    protected Player owner;

    public Fraction(FractionType type) {
        this.type = type;
        this.description = description();
    }

    protected abstract FractionDescription description();

    public FractionType getType() {
        return type;
    }

    public void init(GameContainer game, Player owner) {
        this.game = game;
        this.owner = owner;
    }

    public void turnStarts(ActionHandlers actionHandlers) {
        //default place one new soldier on an occupied location
        List<Location> spawnLocations = owner.getControlledLocations();
        PlaceUnitAction placeUnitAction = actionHandlers.getHandler(PlaceUnitAction.class);
        placeUnitAction.placeSoldier(spawnLocations);
    }

    public void killedMonster(Monster monster) {
        //receive random card as bounty
        owner.getCardHandler().drawRandomCard();
    }

    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = new FightingPower();
        int dice = fighters.stream().map(BattleObject::getPower).reduce(0, Integer::sum);
        power.setDiceCount(dice);
        if (location instanceof Area) {
            powerMutatorArea(power, (Area) location);
        }
        return power;
    }

    protected void powerMutatorArea(FightingPower power, Area area) {
    }

    private void fillSoldier(Soldier soldier, Location location) {
        soldier.setLocation(location);
        soldier.init(game);
    }

    public Collection<Location> getMoveLocations(MapObject object) {
        Location from = object.getLocation();
        return from.getConnectedLocations().stream().filter(to -> canMove(object, from, to)).collect(Collectors.toList());
    }

    protected boolean canMove(MapObject object, Location from, Location to) {
        if (to instanceof Ship) {
            return ((Ship) to).passable(from);
        }
        return true;
    }

    public boolean isHostile(BattleObject battleObject) {
        return battleObject.getOwner() != owner;
    }

    public Soldier createSoldier(Location location) {
        Soldier soldier = new Soldier(owner);
        soldier.setHealth(soldierHealth);
        soldier.setMaxHealth(soldierHealth);
        soldier.setPower(soldierPower);
        fillSoldier(soldier, location);
        return soldier;
    }

    public Commander createCommander(Location location) {
        Commander commander = new Commander(owner);
        commander.setHealth(commanderHealth);
        commander.setMaxHealth(commanderHealth);
        commander.setPower(commanderPower);
        fillSoldier(commander, location);
        return commander;
    }

    public FractionDescription getDescription() {
        return description;
    }
}

package com.broll.gainea.server.core.fractions;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.actions.impl.PlaceUnitAction;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
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
    protected GameContainer game;
    protected Player owner;

    public Fraction(FractionType type) {
        this.type = type;
    }

    public FractionType getType() {
        return type;
    }

    public void init(GameContainer game, Player owner) {
        this.game = game;
        this.owner = owner;
    }

    public void turnStarts(ActionHandlers actionHandlers) {
        //default place one new soldier on an occupied location
        List<Location> spawnLocations = owner.getControlledLocations().collect(Collectors.toList());
        PlaceUnitAction placeUnitAction =actionHandlers.getHandler(PlaceUnitAction.class);
        RequiredActionContext<NT_Action_PlaceUnit> placeUnit = new RequiredActionContext<>(placeUnitAction.placeSoldier(spawnLocations), "Verstärke eine Truppe");
        actionHandlers.getReactionActions().requireAction(owner, placeUnit);
    }

    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemeies, boolean isAttacker) {
        FightingPower power = new FightingPower();
        int dice = fighters.stream().map(BattleObject::getPower).reduce(0, Integer::sum);
        power.setDiceCount(dice);
        return power;
    }

    private void fillSoldier(Soldier soldier, Location location) {
        soldier.setLocation(location);
        soldier.init(game);
    }

    public Collection<Location> getMoveLocations(MapObject object) {
        return object.getLocation().getConnectedLocations();
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

}

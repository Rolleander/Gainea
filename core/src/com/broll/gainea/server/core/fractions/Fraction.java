package com.broll.gainea.server.core.fractions;

import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.required.PlaceUnitAction;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;

public abstract class Fraction extends GameUpdateReceiverAdapter {

    private final static int SOLDIER_HEALTH = 1;
    private final static int SOLDIER_POWER = 1;
    private final static int COMMANDER_HEALTH = 3;
    private final static int COMMANDER_POWER = 3;

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


    public void prepareTurn(ActionHandlers actionHandlers) {
        //default place one new soldier on an occupied location
        List<Location> spawnLocations = owner.getControlledLocations();
        if (spawnLocations.isEmpty()) {
            //player has no more controlled locations. give him a random free one
            Location location = LocationUtils.getRandomFree(game.getMap().getAllAreas());
            if (location == null) {
                //no more free locations, just skip
                return;
            }
            spawnLocations.add(location);
        }
        PlaceUnitAction placeUnitAction = actionHandlers.getHandler(PlaceUnitAction.class);
        placeUnitAction.placeSoldier(owner, spawnLocations);
    }

    public void turnStarted(ActionHandlers actionHandlers) {

    }

    public void killedMonster(Monster monster) {
        //give player monster stars
        owner.getGoalHandler().addStars(monster.getStars());
        //receive random card as bounty
        owner.getCardHandler().drawRandomCard();
    }

    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = new FightingPower();
        int dice = fighters.stream().map(it -> it.getPower().getValue()).reduce(0, Integer::sum);
        power.setDiceCount(dice);
        if (location instanceof Area) {
            powerMutatorArea(power, (Area) location);
        }
        return power;
    }

    protected void powerMutatorArea(FightingPower power, Area area) {
    }

    public List<Location> getMoveLocations(Location from) {
        return from.getConnectedLocations().stream().filter(to -> canMove(from, to)).collect(Collectors.toList());
    }

    protected boolean canMove(Location from, Location to) {
        if (to instanceof Ship) {
            if (!((Ship) to).passable(from)) {
                return false;
            }
        }
        if (from instanceof Ship) {
            if (((Ship) from).getTo() != to) {
                return false;
            }
        }
        return true;
    }

    public boolean isHostile(BattleObject battleObject) {
        return battleObject.getOwner() != owner;
    }

    protected abstract void initSoldier(Soldier soldier);

    protected abstract void initCommander(Commander commander);

    public final Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        initSoldier(soldier);
        return soldier;
    }

    public final Commander createCommander() {
        Commander commander = new Commander(owner);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        initCommander(commander);
        return commander;
    }

    public FractionDescription getDescription() {
        return description;
    }

}

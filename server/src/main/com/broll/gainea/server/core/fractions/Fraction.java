package com.broll.gainea.server.core.fractions;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.actions.required.PlaceUnitAction;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;

public abstract class Fraction extends GameUpdateReceiverAdapter {

    protected final static int SOLDIER_HEALTH = 1;
    protected final static int SOLDIER_POWER = 1;
    protected final static int COMMANDER_HEALTH = 3;
    protected final static int COMMANDER_POWER = 3;

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
        //receive random card as bounty
        owner.getCardHandler().drawRandomCard();
    }

    public boolean isHostile(Unit unit) {
        return unit.getOwner() != owner;
    }

    public abstract Soldier createSoldier();

    public abstract Commander createCommander();

    public FractionDescription getDescription() {
        return description;
    }

    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = new FightingPower(soldier);
        if (context.getLocation() instanceof Area) {
            powerMutatorArea(power, (Area) context.getLocation());
        }
        return power;
    }

    protected void powerMutatorArea(FightingPower power, Area area) {
    }

}

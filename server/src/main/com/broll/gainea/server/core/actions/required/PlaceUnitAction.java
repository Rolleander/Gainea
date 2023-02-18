package com.broll.gainea.server.core.actions.required;

import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.actions.RequiredActionContext;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class PlaceUnitAction extends AbstractActionHandler<NT_Action_PlaceUnit, PlaceUnitAction.Context> {

    private final static Logger Log = LoggerFactory.getLogger(PlaceUnitAction.class);

    private BattleObject unitToPlace;

    class Context extends ActionContext<NT_Action_PlaceUnit> {
        BattleObject unitToPlace;
        List<Location> locations;
        Location selectedLocation;

        public Context(NT_Action_PlaceUnit action) {
            super(action);
        }
    }

    public Pair<BattleObject, Location> placeSoldier(Player player) {
        return placeSoldier(player, player.getControlledLocations());
    }

    public Pair<BattleObject, Location> placeSoldier(Player player, List<Location> locations) {
        return placeUnit(player, createSoldier(player), locations, "Platziere einen Soldat");
    }

    public Pair<BattleObject, Location> placeCommander(Player player) {
        return placeCommander(player, player.getControlledLocations());
    }

    public Pair<BattleObject, Location> placeCommander(Player player, List<Location> locations) {
        return placeUnit(player, createCommander(player), locations, "Platziere deinen Feldherr");
    }

    public Pair<BattleObject, Location> placeMonster(Player player, Monster monster) {
        return null;
    }

    private Soldier createSoldier(Player player) {
        return player.getFraction().createSoldier();
    }

    private Commander createCommander(Player player) {
        return player.getFraction().createCommander();
    }

    public Pair<BattleObject, Location> placeUnit(Player player, BattleObject object, List<Location> locations, String message) {
        if (locations.isEmpty()) {
            throw new RuntimeException("Invalid place unit context: list of locations is empty");
        }
        Log.debug("Place unit action");
        NT_Action_PlaceUnit placeUnit = new NT_Action_PlaceUnit();
        placeUnit.unitToPlace = object.nt();
        placeUnit.possibleLocations = LocationUtils.getLocationNumbers(locations);
        Context context = new Context(placeUnit);
        context.locations = locations;
        context.unitToPlace = object;
        this.unitToPlace = context.unitToPlace;
        actionHandlers.getReactionActions().requireAction(player, new RequiredActionContext<>(context, message));
        Log.trace("Wait for place unit reaction");
        processingBlock.waitFor();
        return Pair.of(object, context.selectedLocation);
    }

    @Override
    public void handleReaction(Context context, NT_Action_PlaceUnit action, NT_Reaction reaction) {
        Log.trace("Handle place unit reaction");
        int nr = reaction.option;
        BattleObject unit = context.unitToPlace;
        Location location = context.locations.get(nr);
        context.selectedLocation = location;
        UnitControl.spawn(game, unit, location);
        processingBlock.resume();
    }

    public BattleObject getUnitToPlace() {
        return unitToPlace;
    }
}

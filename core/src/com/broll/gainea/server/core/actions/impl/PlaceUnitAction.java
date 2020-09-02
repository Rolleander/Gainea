package com.broll.gainea.server.core.actions.impl;

import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.map.Location;

import java.util.List;

public class PlaceUnitAction extends AbstractActionHandler<NT_Action_PlaceUnit, PlaceUnitAction.Context> {

    class Context extends ActionContext<NT_Action_PlaceUnit> {
        BattleObject unitToPlace;
        List<Location> locations;
        PlacedUnitListener placedUnitListener;

        public Context(NT_Action_PlaceUnit action) {
            super(action);
        }
    }

    public Context placeSoldier(List<Location> locations) {
        return placeUnit(createSoldier(), locations);
    }

    public Context placeCommander(List<Location> locations) {
        return placeUnit(createCommander(), locations);
    }

    public Context placeMonster(Monster monster) {
        return null;
    }

    private Soldier createSoldier() {
        return player.getFraction().createSoldier(null);
    }

    private Commander createCommander() {
        return player.getFraction().createCommander(null);
    }

    public Context placeUnit(BattleObject object, List<Location> locations) {
        return placeUnit(object, locations, null);
    }

    public Context placeUnit(BattleObject object, List<Location> locations, PlacedUnitListener listener) {
        NT_Action_PlaceUnit placeUnit = new NT_Action_PlaceUnit();
        placeUnit.unitToPlace = object.nt();
        placeUnit.possibleLocations = locations.stream().mapToInt(Location::getNumber).toArray();
        Context context = new Context(placeUnit);
        context.locations = locations;
        context.unitToPlace = object;
        context.placedUnitListener = listener;
        return context;
    }

    @Override
    public void handleReaction(Context context, NT_Action_PlaceUnit action, NT_Reaction reaction) {
        int nr = reaction.option;
        BattleObject unit = context.unitToPlace;
        Location location = context.locations.get(nr);
        player.getUnits().add(unit);
        unit.setLocation(location);
        location.getInhabitants().add(unit);
        if (context.placedUnitListener != null) {
            context.placedUnitListener.placed(unit, location);
        }
        reactionResult.sendBoardUpdate();
    }

    public interface PlacedUnitListener {
        void placed(BattleObject unit, Location location);
    }
}

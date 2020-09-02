package com.broll.gainea.server.core.actions.impl;

import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.net.NT_Action_MoveUnit;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.map.Location;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoveUnitAction extends AbstractActionHandler<NT_Action_MoveUnit, MoveUnitAction.Context> {

    class Context extends ActionContext<NT_Action_MoveUnit> {
        BattleObject unitToMove;
        List<Location> locations;

        public Context(NT_Action_MoveUnit action) {
            super(action);
        }
    }

    public Context move(BattleObject object, Collection<Location> locations) {
        NT_Action_MoveUnit moveUnit = new NT_Action_MoveUnit();
        moveUnit.possibleLocations = locations.stream().mapToInt(Location::getNumber).toArray();
        moveUnit.unit = object.nt();
        Context context = new Context(moveUnit);
        context.locations = new ArrayList<>(locations);
        context.unitToMove = object;
        return context;
    }

    @Override
    public void handleReaction(Context context, NT_Action_MoveUnit action, NT_Reaction reaction) {
        Location pickedLocation = context.locations.get(reaction.option);
        //perform move

    }
}

package com.broll.gainea.server.core.actions.impl;

import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MoveUnitAction extends AbstractActionHandler<NT_Action_Move, MoveUnitAction.Context> {

    class Context extends ActionContext<NT_Action_Move> {
        List<BattleObject> unitsToMove;
        List<Location> locations;

        public Context(NT_Action_Move action) {
            super(action);
        }
    }

    public Context move(List<BattleObject> objects, Collection<Location> locations) {
        NT_Action_Move moveUnit = new NT_Action_Move();
        moveUnit.possibleLocations = locations.stream().mapToInt(Location::getNumber).toArray();
        moveUnit.units = objects.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        Context context = new Context(moveUnit);
        context.locations = new ArrayList<>(locations);
        context.unitsToMove = objects;
        return context;
    }

    @Override
    public void handleReaction(Context context, NT_Action_Move action, NT_Reaction reaction) {
        game.getProcessingCore().execute(() -> {
            Location pickedLocation = context.locations.get(reaction.option);
            List<MapObject> selectedUnits = new ArrayList<>();
            for (int selection : reaction.options) {
                BattleObject attacker = context.unitsToMove.get(selection);
                selectedUnits.add(attacker);
                context.unitsToMove.remove(attacker);
            }
            context.locations.remove(pickedLocation);
            //perform move
            UnitControl.move(game, selectedUnits, pickedLocation);
            //check for remaining moves
            if (!context.unitsToMove.isEmpty() && !context.locations.isEmpty()) {
                reactionResult.optionalAction(move(context.unitsToMove, context.locations));
            }
        });
    }

}

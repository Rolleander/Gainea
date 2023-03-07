package com.broll.gainea.server.core.actions.optional;

import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.utils.UnitControl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MoveUnitAction extends AbstractActionHandler<NT_Action_Move, MoveUnitAction.Context> {
    private final static Logger Log = LoggerFactory.getLogger(MoveUnitAction.class);

    class Context extends ActionContext<NT_Action_Move> {
        List<BattleObject> unitsToMove;
        Location location;

        public Context(NT_Action_Move action) {
            super(action);
        }
    }

    public Context move(List<BattleObject> objects, Location location) {
        NT_Action_Move moveUnit = new NT_Action_Move();
        moveUnit.location = (short) location.getNumber();
        moveUnit.units = objects.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        Context context = new Context(moveUnit);
        context.location = location;
        context.unitsToMove = objects;
        return context;
    }

    @Override
    public void handleReaction(Context context, NT_Action_Move action, NT_Reaction reaction) {
        game.getProcessingCore().execute(() -> {
            Log.trace("Handle move reaction");
            List<MapObject> selectedUnits = new ArrayList<>();
            Location from = null;
            for (int selection : reaction.options) {
                BattleObject unit = context.unitsToMove.get(selection);
                if (from == null) {
                    from = unit.getLocation();
                } else if (unit.getLocation() != from) {
                    continue;
                }
                selectedUnits.add(unit);
            }
            context.unitsToMove.removeAll(selectedUnits);
            //perform move
            selectedUnits.forEach(MapObject::moved);
            UnitControl.move(game, selectedUnits, context.location);
        });
    }

}

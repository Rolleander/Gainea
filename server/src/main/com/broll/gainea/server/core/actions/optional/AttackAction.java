package com.broll.gainea.server.core.actions.optional;

import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.PlayerUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class AttackAction extends AbstractActionHandler<NT_Action_Attack, AttackAction.Context> {
    private final static Logger Log = LoggerFactory.getLogger(AttackAction.class);

    class Context extends ActionContext<NT_Action_Attack> {
        List<BattleObject> attackers;
        Location location;

        public Context(NT_Action_Attack action) {
            super(action);
        }
    }

    public Context attack(List<BattleObject> attackers, Location attackLocation) {
        NT_Action_Attack action = new NT_Action_Attack();
        action.units = attackers.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        action.location = (short) attackLocation.getNumber();
        Context context = new Context(action);
        context.attackers = attackers;
        context.location = attackLocation;
        return context;
    }

    @Override
    public void handleReaction(Context context, NT_Action_Attack action, NT_Reaction reaction) {
        game.getProcessingCore().execute(() -> {
            Log.trace("Handle attack reaction");
            List<BattleObject> selectedAttackers = new ArrayList<>();
            Location from = null;
            for (int selection : reaction.options) {
                BattleObject attacker = context.attackers.get(selection);
                if (from == null) {
                    from = attacker.getLocation();
                } else if (attacker.getLocation() != from) {
                    continue;
                }
                selectedAttackers.add(attacker);
            }
            context.attackers.removeAll(selectedAttackers);
            if (!selectedAttackers.isEmpty()) {
                selectedAttackers.forEach(BattleObject::attacked);
                startFight(selectedAttackers, context.location);
            }
        });
    }

    private void startFight(List<BattleObject> attackers, Location attackLocation) {
        List<BattleObject> defenders = PlayerUtils.getHostileArmy(player, attackLocation);
        game.getBattleHandler().startBattle(attackers, defenders);
    }
}

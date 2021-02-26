package com.broll.gainea.server.core.actions.optional;

import java.util.ArrayList;
import java.util.List;

import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.networklib.server.impl.ConnectionSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

public class AttackAction extends AbstractActionHandler<NT_Action_Attack, AttackAction.Context> {
    private final static Logger Log = LoggerFactory.getLogger(AttackAction.class);

    class Context extends ActionContext<NT_Action_Attack> {
        List<BattleObject> attackers;
        List<Location> attackLocations;
        Location armyLocation;

        public Context(NT_Action_Attack action) {
            super(action);
        }
    }

    public Context attack(List<BattleObject> attackers, Collection<Location> attackLocations) {
        NT_Action_Attack action = new NT_Action_Attack();
        action.units = attackers.stream().map(BattleObject::nt).toArray(NT_Unit[]::new);
        action.attackLocations = attackLocations.stream().mapToInt(Location::getNumber).toArray();
        Context context = new Context(action);
        context.attackers = attackers;
        context.armyLocation = attackers.get(0).getLocation();
        context.attackLocations = new ArrayList<>(attackLocations);
        return context;
    }

    @Override
    public void handleReaction(Context context, NT_Action_Attack action, NT_Reaction reaction) {
        game.getProcessingCore().execute(() -> {
            Log.trace("Handle attack reaction");
            Location attackLocation = context.attackLocations.get(reaction.option);
            context.attackLocations.remove(attackLocation);
            List<BattleObject> selectedAttackers = new ArrayList<>();
            for (int selection : reaction.options) {
                BattleObject attacker = context.attackers.get(selection);
                selectedAttackers.add(attacker);
            }
            context.attackers.removeAll(selectedAttackers);
            selectedAttackers.forEach(BattleObject::attacked);
            startFight(selectedAttackers, attackLocation);
        });
    }

    private void startFight(List<BattleObject> attackers, Location attackLocation) {
        List<BattleObject> defenders = PlayerUtils.getHostileArmy(player, attackLocation);
        game.getProcessingCore().execute(()-> game.getBattleHandler().startBattle(attackers, defenders));
    }
}

package com.broll.gainea.server.core.actions.impl;

import java.util.ArrayList;
import java.util.List;

import com.broll.gainea.server.core.actions.ActionContext;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.actions.AbstractActionHandler;
import com.broll.gainea.server.core.map.Location;

import java.util.Collection;
import java.util.stream.Collectors;

public class AttackAction extends AbstractActionHandler<NT_Action_Attack, AttackAction.Context> {

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

    public List<BattleObject> getFighters(Player player, Location location) {
        return location.getInhabitants().stream().filter(inhabitant -> {
            if (inhabitant instanceof BattleObject) {
                return ((BattleObject) inhabitant).getOwner() == player;
            }
            return false;
        }).map(o -> (BattleObject) o).collect(Collectors.toList());
    }

    public List<BattleObject> getEnemyArmy(Player player, Location location) {
        return location.getInhabitants().stream().filter(inhabitant -> {
            if (inhabitant instanceof BattleObject) {
                return player.getFraction().isHostile((BattleObject) inhabitant);
            }
            return false;
        }).map(o -> (BattleObject) o).collect(Collectors.toList());
    }

    @Override
    public void handleReaction(Context context, NT_Action_Attack action, NT_Reaction reaction) {
        game.getProcessingCore().execute(() -> {
            Location attackLocation = context.attackLocations.get(reaction.option);
            context.attackLocations.remove(attackLocation);
            List<BattleObject> selectedAttackers = new ArrayList<>();
            for (int selection : reaction.options) {
                BattleObject attacker = context.attackers.get(selection);
                selectedAttackers.add(attacker);
                context.attackers.remove(attacker);
            }
            startFight(selectedAttackers, attackLocation);
            if (!context.attackLocations.isEmpty() && !context.attackers.isEmpty()) {
                //other attack locations and attackers remain => push new attack action
                reactionResult.optionalAction(attack(context.attackers, context.attackLocations));
            }
        });
    }

    private void startFight(List<BattleObject> attackers, Location attackLocation) {
        List<BattleObject> defenders = getEnemyArmy(player, attackLocation);
        game.getBattleHandler().startBattle(attackers, defenders);
    }
}

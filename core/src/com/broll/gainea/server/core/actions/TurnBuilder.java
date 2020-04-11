package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_MoveUnit;
import com.broll.gainea.net.NT_PlayerTurn;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.impl.AttackAction;
import com.broll.gainea.server.core.actions.impl.MoveUnitAction;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.map.Location;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TurnBuilder {

    private GameContainer game;

    private ActionHandlers actionHandlers;
    private Player player;
    private List<ActionContext> turnActions = new ArrayList<>();

    public TurnBuilder(GameContainer game, ActionHandlers actionHandlers) {
        this.game = game;
        this.actionHandlers = actionHandlers;
    }

    public NT_PlayerTurn build(Player player) {
        this.player = player;
        turnActions.clear();
        NT_PlayerTurn turn = new NT_PlayerTurn();
        buildActions();
        turn.actions = turnActions.stream().map(ActionContext::getAction).toArray(NT_Action[]::new);
        turnActions.forEach(game::pushAction);
        return turn;
    }

    public ActionHandlers getActionHandlers() {
        return actionHandlers;
    }

    public void action(ActionContext action) {
        this.turnActions.add(action);
    }

    private void buildActions() {
        //1. create battle actions
        buildAttackActions();
        //2. create unit actions (move targets)
        player.getUnits().forEach(this::buildUnitActions);
        //3. card actions
        player.getCardHandler().onTurnStart(this, actionHandlers);
        //4. fraction actions
        player.getFraction().turnStarts(actionHandlers);
    }

    private void buildAttackActions() {
        AttackAction attackHandler = actionHandlers.getHandler(AttackAction.class);
        player.getControlledLocations().forEach(location -> {
            List<Location> attackLocations = location.getConnectedLocations().stream().filter(
                    attackLocation -> !attackHandler.getEnemyArmy(player, attackLocation).isEmpty()).collect(Collectors.toList());
            if (!attackLocations.isEmpty()) {
                action(attackHandler.attack(location, attackLocations));
            }
        });
    }

    private void buildUnitActions(BattleObject battleObject) {
        //create move actions for each unit
        AttackAction attackHandler = actionHandlers.getHandler(AttackAction.class);
        MoveUnitAction moveHandler = actionHandlers.getHandler(MoveUnitAction.class);
        List<Location> moveLocations = player.getFraction().getMoveLocations(battleObject).stream().filter(moveLocation ->
                attackHandler.getEnemyArmy(player, moveLocation).isEmpty()
        ).collect(Collectors.toList());
        if (!moveLocations.isEmpty()) {
            action(moveHandler.move(battleObject, moveLocations));
        }
    }

}

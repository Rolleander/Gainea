package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_PlayerTurn;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.impl.AttackAction;
import com.broll.gainea.server.core.actions.impl.MoveUnitAction;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.Collection;
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
        //1. fraction
        player.getFraction().prepareTurn(actionHandlers);
        //2. create move and battle actions
        buildMoveAndAttackActions();
        //3. card actions
        player.getCardHandler().onTurnStart(this, actionHandlers);
    }

    private void buildMoveAndAttackActions() {
        AttackAction attackHandler = actionHandlers.getHandler(AttackAction.class);
        MoveUnitAction moveHandler = actionHandlers.getHandler(MoveUnitAction.class);
        player.getControlledLocations().forEach(location -> {
            List<Location> moveLocations = player.getFraction().getMoveLocations(location);
            List<Location> attackLocations = moveLocations.stream().filter(
                    moveLocation -> !PlayerUtils.getHostileArmy(player, moveLocation).isEmpty()).collect(Collectors.toList());
            moveLocations.removeAll(attackLocations);
            List<BattleObject> units = player.getUnits().stream().filter(it -> location == it.getLocation() && it.isRooted() == false).collect(Collectors.toList());
            if (!attackLocations.isEmpty()) {
                action(attackHandler.attack(units, attackLocations));
            }
            if (!moveLocations.isEmpty()) {
                action(moveHandler.move(units, moveLocations));
            }
        });
    }
}

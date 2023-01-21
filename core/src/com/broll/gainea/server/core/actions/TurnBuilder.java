package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.optional.AttackAction;
import com.broll.gainea.server.core.actions.optional.CardAction;
import com.broll.gainea.server.core.actions.optional.MoveUnitAction;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TurnBuilder {

    private GameContainer game;
    private ActionHandlers actionHandlers;

    public TurnBuilder(GameContainer game, ActionHandlers actionHandlers) {
        this.game = game;
        this.actionHandlers = actionHandlers;
    }

    public NT_PlayerTurnActions build(Player player) {
        game.clearActions();
        NT_PlayerTurnActions turn = new NT_PlayerTurnActions();
        List<ActionContext> actions = new ArrayList<>();
        actions.addAll(buildMoveAndAttackActions(player));
        actions.addAll(buildCardActions(player));
        turn.actions = actions.stream().map(ActionContext::getAction).toArray(NT_Action[]::new);
        actions.forEach(game::pushAction);
        return turn;
    }

    private List<ActionContext> buildMoveAndAttackActions(Player player) {
        List<ActionContext> actions = new ArrayList<>();
        AttackAction attackHandler = actionHandlers.getHandler(AttackAction.class);
        MoveUnitAction moveHandler = actionHandlers.getHandler(MoveUnitAction.class);
        player.getControlledLocations().forEach(location -> {
            List<Location> moveLocations = player.getFraction().getMoveLocations(location);
            List<Location> attackLocations = moveLocations.stream().filter(
                    moveLocation -> !PlayerUtils.getHostileArmy(player, moveLocation).isEmpty()).collect(Collectors.toList());
            moveLocations.removeAll(attackLocations);
            List<BattleObject> moveableUnits = player.getUnits().stream().filter(it -> location == it.getLocation() && it.canMove()).collect(Collectors.toList());
            List<BattleObject> attackingUnits = player.getUnits().stream().filter(it -> location == it.getLocation() && it.canAttack()).collect(Collectors.toList());
            if (!attackLocations.isEmpty() && !attackingUnits.isEmpty()) {
                actions.add(attackHandler.attack(attackingUnits, attackLocations));
            }
            if (!moveLocations.isEmpty() && !moveableUnits.isEmpty()) {
                actions.add(moveHandler.move(moveableUnits, moveLocations));
            }
        });
        return actions;
    }

    private List<ActionContext> buildCardActions(Player player) {
        CardAction cardAction = actionHandlers.getHandler(CardAction.class);
        return player.getCardHandler().getCards().stream().filter(Card::isPlayable).map(card -> (ActionContext) cardAction.playableCard(player, card)).collect(Collectors.toList());
    }

}

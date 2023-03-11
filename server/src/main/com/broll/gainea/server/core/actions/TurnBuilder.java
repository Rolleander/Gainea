package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.optional.AttackAction;
import com.broll.gainea.server.core.actions.optional.CardAction;
import com.broll.gainea.server.core.actions.optional.MoveUnitAction;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;

import org.apache.commons.collections4.map.MultiValueMap;

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
        MultiValueMap<Location, Unit> moveableTo = new MultiValueMap<>();
        MultiValueMap<Location, Unit> attackableTo = new MultiValueMap<>();
        player.getUnits().stream().filter(Unit::isControllable).forEach(unit -> {
            List<Location> walkableLocations = unit.getLocation().getConnectedLocations().stream().filter(unit::canMoveTo).collect(Collectors.toList());
            if (unit.hasRemainingAttack()) {
                List<Location> attackableLocations = walkableLocations.stream().filter(it -> PlayerUtils.hasHostileArmy(player, it)).collect(Collectors.toList());
                walkableLocations.removeAll(attackableLocations);
                attackableLocations.forEach(it -> attackableTo.put(it, unit));
            }
            if (unit.hasRemainingMove()) {
                walkableLocations.forEach(it -> moveableTo.put(it, unit));
            }
        });
        moveableTo.keySet().forEach(moveTo -> actions.add(moveHandler.move((List<Unit>) moveableTo.getCollection(moveTo), moveTo)));
        attackableTo.keySet().forEach(attackTo -> actions.add(attackHandler.attack((List<Unit>) attackableTo.getCollection(attackTo), attackTo)));
        return actions;
    }

    private List<ActionContext> buildCardActions(Player player) {
        CardAction cardAction = actionHandlers.getHandler(CardAction.class);
        return player.getCardHandler().getCards().stream().filter(Card::isPlayable).map(card -> (ActionContext) cardAction.playableCard(player, card)).collect(Collectors.toList());
    }

}

package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReactionHandler {

    private final static Logger Log = LoggerFactory.getLogger(ReactionHandler.class);
    private GameContainer game;
    private Map<RequiredActionContext, RequiredAction> requiredActions = Collections.synchronizedMap(new HashMap<>());
    private ActionHandlers actionHandlers;

    public ReactionHandler(GameContainer game, ActionHandlers actionHandlers) {
        this.game = game;
        this.actionHandlers = actionHandlers;
    }

    public void requireAction(Player target, RequiredActionContext context) {
        RequiredAction action = new RequiredAction();
        action.player = target;
        action.context = context;
        game.pushAction(context);
        requiredActions.put(context, action);
    }

    public ActionHandlers getActionHandlers() {
        return actionHandlers;
    }

    public synchronized void finishedProcessing() {
        Log.trace("Processing core finished");
        tryContinueTurn();
    }

    private void tryContinueTurn() {
        if (requiredActions.isEmpty() && !game.isGameOver()) {
            if (game.getCurrentPlayer().isActive()) {
                continueTurn();
            } else {
                if (game.getActivePlayers().stream().allMatch(it -> it.getServerPlayer().isBot())) {
                    //only bots remaining, end game
                    game.end();
                } else {
                    //end inactive players turn
                    actionHandlers.getReactionActions().endTurn();
                }
            }
        }
    }

    private void continueTurn() {
        Player activePlayer = game.getCurrentPlayer();
        NT_PlayerTurnActions turn = game.getTurnBuilder().build(activePlayer);
        Log.trace("Continue " + activePlayer + " turn [" + turn.actions.length + " optional actions]");
        if (turn.actions.length == 0) {
            //no more actions available, player can only end turn
            activePlayer.getServerPlayer().sendTCP(new NT_EndTurn());
        } else {
            activePlayer.getServerPlayer().sendTCP(turn);
        }
    }

    public void playerReconnected(Player player) {
        Log.info(player + " reconnected to game");
        //re send required actions for this player
        requiredActions.values().stream().filter(ra -> ra.player == player).findFirst().ifPresent(requiredAction -> {
            player.getServerPlayer().sendTCP(requiredAction.context.nt());
        });
        if (game.isPlayersTurn(player) && !game.getProcessingCore().isBusy() && !game.getBattleHandler().isBattleActive()) {
            //resend remaining optional actions
            tryContinueTurn();
        }
    }

    public synchronized boolean hasRequiredActionFor(Player player) {
        return requiredActions.values().stream().anyMatch(ra -> ra.player == player);
    }

    public void handle(Player gamePlayer, ActionContext actionContext, NT_Reaction reaction) {
        if (game.getBattleHandler().isBattleActive()) {
            //ignore reactions while fight is going on
            Log.warn("Ignore player reaction because of fight!");
            return;
        }
        if (requiredActions.isEmpty()) {
            if (game.getProcessingCore().isBusy()) {
                //player action is not handled, game still processing actions
                Log.warn("Ignore player optional action because processing core is busy!");
                return;
            }
            //handle optional action
            handleReaction(gamePlayer, actionContext, reaction);
        } else {
            //handle required actions only
            RequiredAction ra = requiredActions.get(actionContext);
            if (ra != null) {
                if (gamePlayer == ra.player) {
                    handleReaction(gamePlayer, ra.context.getActionContext(), reaction);
                    //consume required action
                    requiredActions.remove(actionContext);
                    game.consumeAction(ra.context.getAction().actionId);
                } else {
                    Log.warn("Ingore required reaction because its sent by the wrong player");
                }
            } else {
                Log.warn("Ingore optional action because of required action");
            }
        }
    }

    private void handleReaction(Player gamePlayer, ActionContext actionContext, NT_Reaction reaction) {
        NT_Action action = actionContext.getAction();
        CustomReactionHandler customHandler = actionContext.getCustomHandler();
        ActionCompletedListener completionListener = actionContext.getCompletionListener();
        if (customHandler != null) {
            customHandler.handleReaction(actionContext, reaction);
        } else {
            AbstractActionHandler actionHandler = actionHandlers.getHandlerForAction(action.getClass());
            if (actionHandler != null) {
                actionHandler.update(gamePlayer);
                actionHandler.handleReaction(actionContext, action, reaction);
            } else {
                Log.error("No actionHandler found for action " + action);
            }
        }
        if (completionListener != null) {
            completionListener.completed(actionContext);
        }
        game.consumeAction(action.actionId);
    }

    private class RequiredAction {
        public RequiredActionContext context;
        public Player player;
    }
}

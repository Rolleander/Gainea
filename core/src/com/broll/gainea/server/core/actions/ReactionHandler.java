package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;
import com.esotericsoftware.minlog.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class ReactionHandler {

    private GameContainer game;
    private ReactionActions reactionActions;
    private Map<RequiredActionContext, RequiredAction> requiredActions = Collections.synchronizedMap(new HashMap<>());
    private ActionHandlers actionHandlers;

    public ReactionHandler(GameContainer game, ActionHandlers actionHandlers) {
        this.game = game;
        this.actionHandlers = actionHandlers;
        this.reactionActions = actionHandlers.getReactionActions();
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
        if (requiredActions.isEmpty()) {
            continueTurn();
        }
    }

    private void continueTurn(){
        Player activePlayer = game.getPlayers().get(game.getCurrentPlayer());
        NT_PlayerTurnActions turn = game.getTurnBuilder().build(activePlayer);
        if(turn.actions.length == 0){
            //no more actions available, player can only end turn
            activePlayer.getServerPlayer().sendTCP(new NT_EndTurn());
        }
        else{
            activePlayer.getServerPlayer().sendTCP(turn);
        }
    }

    public void playerReconnected(Player player) {
        Log.info(player + " reconnected to game");
        //re send required actions for this player
        requiredActions.values().stream().filter(ra -> ra.player == player).forEach(requiredAction -> {
            player.getServerPlayer().sendTCP(requiredAction.context.nt());
        });
        if (game.isPlayersTurn(player)) {
            //re send remaining optional actions
            finishedProcessing();
        }
    }

    public synchronized boolean hasRequiredActionFor(Player player) {
        return requiredActions.values().stream().filter(ra -> ra.player == player).findFirst().isPresent();
    }

    public void handle(Player gamePlayer, ActionContext actionContext, NT_Reaction reaction) {
        if (game.getBattleHandler().isBattleActive()) {
            //ignore reactions while fight is going on
            return;
        }
        if (requiredActions.isEmpty()) {
            if (game.getProcessingCore().isBusy()) {
                //player action is not handled, game still processing actions
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
                }
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

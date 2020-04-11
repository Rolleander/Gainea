package com.broll.gainea.server.core.actions;

import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ReactionHandler {

    private GameContainer game;
    private ReactionActions reactionActions;
    private Map<ActionContext, RequiredAction> requiredActions = Collections.synchronizedMap(new HashMap<>());
    private ActionHandlers actionHandlers;

    public ReactionHandler(GameContainer game, ActionHandlers actionHandlers) {
        this.game = game;
        this.actionHandlers = actionHandlers;
        this.reactionActions = actionHandlers.getReactionActions();
    }

    public void requireAction(Player target, ActionContext requiredAction) {
        RequiredAction ra = new RequiredAction();
        ra.player = target;
        ra.context = requiredAction;
        requiredActions.put(requiredAction, ra);
    }

    public ActionHandlers getActionHandlers() {
        return actionHandlers;
    }

    public synchronized boolean hasRequiredActionFor(Player player) {
        return requiredActions.values().stream().filter(ra -> ra.player == player).findFirst().isPresent();
    }

    public void handle(Player gamePlayer, ActionContext actionContext, NT_Reaction reaction) {
        if (game.getBattleHandler().isBattleActive()) {
            //ignore reactions while fight is going one
            return;
        }
        if (requiredActions.isEmpty()) {
            //handle optional action
            handleReaction(gamePlayer, actionContext, reaction);
        } else {
            //handle required actions only
            RequiredAction ra = requiredActions.get(actionContext);
            if (gamePlayer == ra.player) {
                if (handleReaction(gamePlayer, actionContext, reaction)) {
                    //consume required action
                    requiredActions.remove(actionContext);
                }
            }
        }
    }

    private boolean handleReaction(Player gamePlayer, ActionContext actionContext, NT_Reaction reaction) {
        NT_Action action = actionContext.getAction();
        CustomReactionHandler customHandler = actionContext.getCustomHandler();
        ActionCompletedListener completionListener = actionContext.getCompletionListener();
        boolean consumeAction = true;
        if (customHandler != null) {
            customHandler.handleReaction(actionContext, reaction);
        } else {
            AbstractActionHandler actionHandler = actionHandlers.getHandlerForAction(action.getClass());
            if(actionHandler!=null){
                actionHandler.update(gamePlayer);
                actionHandler.handleReaction(actionContext, action, reaction);
                consumeAction = !actionHandler.isKeepAction();
            }
        }
        if (completionListener != null) {
            completionListener.completed(actionContext);
        }
        if (consumeAction) {
            game.consumeAction(action.actionId);
            return true;
        }
        return false;
    }

    private class RequiredAction {
        public ActionContext context;
        public Player player;
    }
}

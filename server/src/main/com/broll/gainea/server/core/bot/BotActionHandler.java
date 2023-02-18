package com.broll.gainea.server.core.bot;

import com.broll.gainea.misc.PackageLoader;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.strategy.BotStrategy;
import com.broll.gainea.server.core.player.Player;

import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class BotActionHandler {

    private final static String PACKAGE_PATH = "com.broll.gainea.server.core.bot.impl";
    private GameContainer game;
    private Player bot;
    private BotStrategy strategy;
    private Map<Class<? extends NT_Action>, BotAction> actions = new HashMap<>();
    private BotDecision endTurnDecision = new EndTurnDecision();

    public BotActionHandler(GameContainer game, Player bot, BotStrategy strategy) {
        this.game = game;
        this.bot = bot;
        this.strategy = strategy;
        endTurnDecision.init(game, bot, strategy);
        new PackageLoader<BotAction>(BotAction.class, PACKAGE_PATH).instantiateAll().forEach(a -> initAction(a.getActionClass(), a));
    }

    public BotAction getActionHandler(Class<? extends BotAction> clazz) {
        return actions.values().stream().filter(it -> clazz.isInstance(it)).findFirst().orElse(null);
    }

    private void initAction(Class<? extends NT_Action> actionClass, BotAction botAction) {
        botAction.init(game, bot, strategy);
        actions.put(actionClass, botAction);
    }

    public NT_Reaction react(NT_Action action) {
        BotAction botAction = actions.get(action.getClass());
        if (botAction == null) {
            throw new RuntimeException("Bot unable to react to " + action);
        }
        return botAction.perform(action);
    }

    public Pair<BotDecision, NT_Action> bestScore(NT_PlayerTurnActions turn) {
        BotDecision decision = endTurnDecision;
        float score = endTurnDecision.score(null, turn);
        NT_Action chosenAction = null;
        for (NT_Action action : turn.actions) {
            BotAction botAction = actions.get(action.getClass());
            if (botAction != null) {
                float actionScore = botAction.score(action, turn);
                if (actionScore > score) {
                    score = actionScore;
                    decision = botAction;
                    chosenAction = action;
                }
            }
        }
        return Pair.of(decision, chosenAction);
    }

}

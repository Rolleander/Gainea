package com.broll.gainea.server.core.bot;

import com.broll.gainea.misc.PackageLoader;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.strategy.BotStrategy;
import com.broll.gainea.server.core.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class BotActionHandler {
    private final static Logger Log = LoggerFactory.getLogger(BotActionHandler.class);
    private final static String PACKAGE_PATH = "com.broll.gainea.server.core.bot.impl";
    private GameContainer game;
    private Player bot;
    private BotStrategy strategy;
    private Map<Class<? extends NT_Action>, BotAction> actions = new HashMap<>();

    private EndTurnOption endTurnOption = new EndTurnOption();

    public BotActionHandler(GameContainer game, Player bot, BotStrategy strategy) {
        this.game = game;
        this.bot = bot;
        this.strategy = strategy;
        new PackageLoader<>(BotAction.class, PACKAGE_PATH).instantiateAll().forEach(a -> initAction(a.getActionClass(), a));
    }

    public BotAction getActionHandler(Class<? extends BotAction> clazz) {
        return actions.values().stream().filter(clazz::isInstance).findFirst().orElse(null);
    }

    private void initAction(Class<? extends NT_Action> actionClass, BotAction botAction) {
        botAction.init(game, bot, strategy, this);
        actions.put(actionClass, botAction);
    }

    public NT_Reaction react(NT_Action action) {
        BotAction botAction = actions.get(action.getClass());
        if (botAction == null) {
            throw new RuntimeException("Bot unable to react to " + action);
        }
        return botAction.perform(action);
    }

    public Object createBestReaction(NT_PlayerTurnActions turn) {
        BotOptionalAction bestAction = null;
        BotOptionalAction.BotOption bestOption = endTurnOption;
        NT_Action ntAction = null;
        for (NT_Action action : turn.actions) {
            BotOptionalAction botAction = (BotOptionalAction) actions.get(action.getClass());
            BotOptionalAction.BotOption botOption = botAction.score(action);
            if (botOption != null) {
                Log.trace(bot + " scored [" + botOption.getScore() + "] for option " + botOption);
                if (botOption.getScore() > bestOption.getScore()) {
                    bestOption = botOption;
                    bestAction = botAction;
                    ntAction = action;
                }
            }
        }
        Log.trace(bot + " picked best option: " + bestOption);
        if (bestOption == endTurnOption) {
            return new NT_EndTurn();
        }
        bestAction.setSelectedOption(bestOption);
        return bestAction.perform(ntAction);
    }

    private class EndTurnOption extends BotOptionalAction.BotOption {
        public EndTurnOption() {
            super(0);
        }

        @Override
        public String toString() {
            return "endturn";
        }
    }


}

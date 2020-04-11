package com.broll.gainea.server.core.goals;

import java.util.List;

import com.broll.gainea.misc.PackageLoader;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.player.Player;

import java.util.function.Function;

public class GoalStorage {

    private final static String PACKAGE_PATH = "com.broll.gainea.server.core.goals.impl";
    private final List<Class<? extends AbstractGoal>> goalClasses;
    private PackageLoader<AbstractGoal> loader;
    private GameContainer game;
    private ActionHandlers actionHandlers;

    public GoalStorage(GameContainer gameContainer, ActionHandlers actionHandlers) {
        this.game = gameContainer;
        this.actionHandlers = actionHandlers;
        loader = new PackageLoader<>(AbstractGoal.class, PACKAGE_PATH);
        goalClasses = loader.getClasses();
    }

    public void assignNewRandomGoal(Player player) {
        assignNewGoal(player, goal -> true);
    }

    public void assignNewGoal(Player player, GoalDifficulty difficulty) {
        assignNewGoal(player, goal -> goal.getDifficulty() == difficulty);
    }

    public void assignNewGoal(Player player, Function<AbstractGoal, Boolean> condition) {
        AbstractGoal goal = newGoal(player, condition);
        if (goal != null) {
            //assign goal to player
            player.getGoalHandler().newGoal(goal);
        } else {
            //no more goals found

        }
    }

    public AbstractGoal newGoal(Player forPlayer, Function<AbstractGoal, Boolean> condition) {
        for (Class clazz : goalClasses) {
            AbstractGoal goal = loader.instantiate(clazz);
            if (goal.init(game, forPlayer)) {
                if (condition.apply(goal)) {
                    goalClasses.remove(clazz);
                    return goal;
                }
            }
        }
        //no more matching goal found
        return null;
    }

}

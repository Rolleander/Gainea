package com.broll.gainea.server.core.goals;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.broll.gainea.misc.PackageLoader;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.goals.impl.e1.G_GaineaShips;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.init.GoalTypes;
import com.broll.networklib.server.impl.ConnectionSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;
import java.util.stream.Collectors;

public class GoalStorage {

    private final static Logger Log = LoggerFactory.getLogger(GoalStorage.class);
    private final static String PACKAGE_PATH = "com.broll.gainea.server.core.goals.impl";
    private List<Class<? extends AbstractGoal>> goalClasses;
    private PackageLoader<AbstractGoal> loader;
    private GameContainer game;
    private ActionHandlers actionHandlers;
    private GoalTypes goalTypes;

    public GoalStorage(GameContainer gameContainer, ActionHandlers actionHandlers, GoalTypes goalTypes) {
        this.game = gameContainer;
        this.actionHandlers = actionHandlers;
        this.loader = new PackageLoader<>(AbstractGoal.class, PACKAGE_PATH);
        this.goalTypes = goalTypes;
        initGoals();
    }

    private void initGoals() {
        this.goalClasses = loader.getClasses().stream().collect(Collectors.toList());
        Collections.shuffle(goalClasses);
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
            //no goal found for condition
            if (goalClasses.isEmpty()) {
                //refresh goals
                initGoals();
                //try again to find one
                goal = newGoal(player, condition);
                if (goal != null) {
                    //assign goal to player
                    player.getGoalHandler().newGoal(goal);
                } else {
                    Log.error("No goal for condition " + condition + " was found");
                }
            } else {
                Log.error("No goal for condition " + condition + " was found in remaining goals");
            }
        }
    }

    public AbstractGoal newGoal(Player forPlayer, Function<AbstractGoal, Boolean> condition) {
        for (Class clazz : goalClasses) {
            AbstractGoal goal = loader.instantiate(clazz);
            if (goalTypes.contains(goal.getDifficulty())) {
                if (goal.init(game, forPlayer)) {
                    if (condition.apply(goal)) {
                        goalClasses.remove(clazz);
                        return goal;
                    }
                }
            }
        }
        //no more matching goals found
        return null;
    }

}

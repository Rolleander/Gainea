package com.broll.gainea.server.core.goals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.broll.gainea.net.NT_Event_FinishedGoal;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.GameUtils;
import com.esotericsoftware.minlog.Log;

public abstract class AbstractGoal extends GameUpdateReceiverAdapter {

    protected String text;
    private GoalDifficulty difficulty;
    private String restrictionInfo;
    private ExpansionType[] requiredExpansions;
    protected GameContainer game;
    protected Player player;
    private boolean finished = false;

    public AbstractGoal(GoalDifficulty difficulty, String text) {
        this.difficulty = difficulty;
        this.text = text;
    }

    public boolean init(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
        Log.info("init goal "+text+" with player "+player);
        return validForGame();
    }

    protected void setCustomRestrictionInfo(String restrictionInfo) {
        this.restrictionInfo = restrictionInfo;
    }

    protected void setExpansionRestriction(ExpansionType... expansions) {
        this.requiredExpansions = expansions;
        this.restrictionInfo = Arrays.stream(expansions).map(ExpansionType::getName).collect(Collectors.joining(","));
    }

    protected boolean validForGame() {
        if (requiredExpansions == null) {
            return true;
        }
        List<ExpansionType> activeExpansions = game.getMap().getActiveExpansionTypes();
        for (ExpansionType type : requiredExpansions) {
            if (!activeExpansions.contains(type)) {
                //required expansion of goal is not active in this game
                return false;
            }
        }
        return true;
    }

    protected synchronized void success() {
        if (!finished) {
            player.getGoalHandler().removeGoal(this);
            player.getGoalHandler().addPoints(difficulty.getPoints());
            NT_Event_FinishedGoal finishedGoal = new NT_Event_FinishedGoal();
            finishedGoal.player = player.getServerPlayer().getId();
            finishedGoal.goal = this.nt();
            GameUtils.sendUpdate(game, finishedGoal);
            finished = true;
        }
    }

    public abstract void check();

    public GoalDifficulty getDifficulty() {
        return difficulty;
    }

    public NT_Goal nt() {
        NT_Goal goal = new NT_Goal();
        goal.description = text;
        goal.points = difficulty.getPoints();
        goal.restriction = restrictionInfo;
        return goal;
    }

}

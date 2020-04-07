package com.broll.gainea.server.core.goals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.gainea.server.core.player.Player;

public abstract class AbstractGoal {

    private String text;
    private GoalDifficulty difficulty;
    private String restrictionInfo;
    private ExpansionType[] requiredExpansions;
    private GameContainer game;
    private Player player;

    public AbstractGoal(GoalDifficulty difficulty, String text) {
        this.difficulty = difficulty;
        this.text = text;
    }

    public boolean init(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
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
        List<ExpansionType> activeExpansions = game.getMap().getActiveExpansionTypes();
        for (ExpansionType type : requiredExpansions) {
            if (!activeExpansions.contains(type)) {
                //required expansion of goal is not active in this game
                return false;
            }
        }
        return true;
    }

    public abstract boolean checkCondition();

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

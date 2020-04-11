package com.broll.gainea.server.core.goals;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GoalHandler {

    private int score;
    private List<AbstractGoal> goals = new ArrayList<>();

    public GoalHandler(GameContainer game, Player player) {
    }

    private void addPoints(int points) {
        this.score += points;
    }

    public int getScore() {
        return score;
    }

    public List<AbstractGoal> getGoals() {
        return goals;
    }

    public void removeGoal(AbstractGoal oldGoal) {
        goals.remove(oldGoal);
    }

    public void newGoal(AbstractGoal goal) {
        this.goals.add(goal);
    }

}

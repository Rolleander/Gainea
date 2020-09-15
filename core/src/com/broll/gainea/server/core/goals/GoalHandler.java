package com.broll.gainea.server.core.goals;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GoalHandler {

    private int score;
    private int stars;
    private List<AbstractGoal> goals = new ArrayList<>();
    private GameContainer game;
    private Player player;

    public GoalHandler(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void addPoints(int points) {
        this.score += points;
        game.getUpdateReceiver().earnedStars(player, points);
    }

    public void addStars(int stars) {
        this.stars += stars;
    }

    public int getScore() {
        return score;
    }

    public int getStars() {
        return stars;
    }

    public List<AbstractGoal> getGoals() {
        return goals;
    }

    public void removeGoal(AbstractGoal oldGoal) {
        goals.remove(oldGoal);
        game.getUpdateReceiver().unregister(oldGoal);
    }

    public void newGoal(AbstractGoal goal) {
        this.goals.add(goal);
        game.getUpdateReceiver().register(goal);
    }

}

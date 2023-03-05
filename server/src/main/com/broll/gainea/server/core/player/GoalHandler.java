package com.broll.gainea.server.core.player;

import com.broll.gainea.net.NT_Event_OtherPlayerReceivedGoal;
import com.broll.gainea.net.NT_Event_ReceivedGoal;
import com.broll.gainea.net.NT_Event_ReceivedPoints;
import com.broll.gainea.net.NT_Event_ReceivedStars;
import com.broll.gainea.net.NT_Event_RemoveGoal;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;

import java.util.ArrayList;
import java.util.List;

public class GoalHandler {

    private int score;
    private int stars;
    private List<Goal> goals = new ArrayList<>();
    private GameContainer game;
    private Player player;

    public GoalHandler(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void addPoints(int points) {
        this.score += points;
        NT_Event_ReceivedPoints nt = new NT_Event_ReceivedPoints();
        nt.player = player.getServerPlayer().getId();
        nt.points = points;
        GameUtils.sendUpdate(game, nt);
        ProcessingUtils.pause(500);
        if (GameUtils.isGameEnd(game)) {
            game.end();
        }
    }

    public void addStars(int stars) {
        this.stars += stars;
        NT_Event_ReceivedStars nt = new NT_Event_ReceivedStars();
        nt.player = player.getServerPlayer().getId();
        nt.stars = stars;
        GameUtils.sendUpdate(game, nt);
        ProcessingUtils.pause(500);
        game.getUpdateReceiver().earnedStars(player, stars);
    }

    public int getScore() {
        return score;
    }

    public int getStars() {
        return stars;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public void removeGoal(Goal oldGoal) {
        goals.remove(oldGoal);
        game.getUpdateReceiver().unregister(oldGoal);
        NT_Event_RemoveGoal nt = new NT_Event_RemoveGoal();
        nt.goal = oldGoal.nt();
        player.getServerPlayer().sendTCP(nt);
    }

    public void newGoal(Goal goal) {
        this.goals.add(goal);
        game.getUpdateReceiver().register(goal);
        NT_Event_ReceivedGoal nt = new NT_Event_ReceivedGoal();
        nt.sound = "chime.ogg";
        nt.goal = goal.nt();
        NT_Event_OtherPlayerReceivedGoal nt2 = new NT_Event_OtherPlayerReceivedGoal();
        nt2.player = player.getServerPlayer().getId();
        GameUtils.sendUpdate(game, player, nt, nt2);
        //directly check goal for completion
        game.getProcessingCore().execute(goal::check, 100);
    }

    public NT_Goal[] ntGoals() {
        return goals.stream().map(Goal::nt).toArray(NT_Goal[]::new);
    }
}

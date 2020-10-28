package com.broll.gainea.server.core.goals;

import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedGoal;
import com.broll.gainea.net.NT_Event_ReceivedGoal;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUtils;

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
        if (this.score >= game.getGameSettings().getPointLimit()) {
            game.end();
        }
    }

    public void addStars(int stars) {
        this.stars += stars;
        game.getUpdateReceiver().earnedStars(player, stars);
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
        NT_Event_ReceivedGoal nt = new NT_Event_ReceivedGoal();
        nt.goal = goal.nt();
        NT_Event_OtherPlayerReceivedGoal nt2 = new NT_Event_OtherPlayerReceivedGoal();
        nt2.player = player.getServerPlayer().getId();
        GameUtils.sendUpdate(game, player, nt, nt2);
        //directly check goal for completion
        game.getProcessingCore().execute(goal::check, 2000);
    }

    public NT_Goal[] ntGoals() {
        return goals.stream().map(AbstractGoal::nt).toArray(NT_Goal[]::new);
    }
}

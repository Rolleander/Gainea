package com.broll.gainea.server.core.player;

import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedGoal;
import com.broll.gainea.net.NT_Event_ReceivedGoal;
import com.broll.gainea.net.NT_Event_ReceivedPoints;
import com.broll.gainea.net.NT_Event_ReceivedStars;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.ProcessingUtils;

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
        NT_Event_ReceivedPoints nt = new NT_Event_ReceivedPoints();
        nt.player = player.getServerPlayer().getId();
        nt.points = points;
        GameUtils.sendUpdate(game, nt);
        ProcessingUtils.pause(500);
        if (this.score >= game.getGameSettings().getPointLimit()) {
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
        nt.sound = "chime.ogg";
        nt.goal = goal.nt();
        NT_Event_OtherPlayerReceivedGoal nt2 = new NT_Event_OtherPlayerReceivedGoal();
        nt2.player = player.getServerPlayer().getId();
        GameUtils.sendUpdate(game, player, nt, nt2);
        //directly check goal for completion
        game.getProcessingCore().execute(goal::check, 100);
    }

    public NT_Goal[] ntGoals() {
        return goals.stream().map(AbstractGoal::nt).toArray(NT_Goal[]::new);
    }
}

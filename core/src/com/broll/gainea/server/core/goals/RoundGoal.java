package com.broll.gainea.server.core.goals;

import com.broll.gainea.server.core.player.Player;

public abstract class RoundGoal extends Goal {

    private int roundTarget;
    private int rounds;

    public RoundGoal(GoalDifficulty difficulty, String text, int rounds) {
        super(difficulty, text);
        this.roundTarget = rounds;
        this.setProgressionGoal(roundTarget);
    }

    @Override
    public void turnStarted(Player player) {
        if (player == this.player && game.getRounds() > 1) {
            check();
        }
    }

    protected void progressRound() {
        rounds++;
        updateProgression(rounds);
        if (rounds >= roundTarget) {
            success();
        }
    }

    protected void resetRounds() {
        rounds = 0;
        updateProgression(rounds);
    }

}

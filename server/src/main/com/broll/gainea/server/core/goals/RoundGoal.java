package com.broll.gainea.server.core.goals;

import com.broll.gainea.server.core.player.Player;

public abstract class RoundGoal extends Goal {

    private int roundTarget;
    private int rounds;
    private int turns;

    public RoundGoal(GoalDifficulty difficulty, String text, int rounds) {
        super(difficulty, text);
        this.roundTarget = rounds;
        this.setProgressionGoal(roundTarget);
    }

    @Override
    public void turnStarted(Player player) {
        if (game.getCurrentTurn() > 0 || game.getRounds() > 1) {
            turns++;
            if (turns > game.getAllPlayers().size()) {
                check();
            }
        }
    }

    protected void progressRound() {
        turns = 0;
        rounds++;
        updateProgression(rounds);
        if (rounds >= roundTarget) {
            success();
        }
    }

    protected void resetRounds() {
        turns = 0;
        rounds = 0;
        updateProgression(rounds);
    }

}

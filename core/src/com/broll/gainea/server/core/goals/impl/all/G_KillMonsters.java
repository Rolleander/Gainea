package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.player.Player;

public class G_KillMonsters extends Goal {
    private final int starsTarget;
    private int stars;

    public G_KillMonsters() {
        this(GoalDifficulty.EASY, 7);
    }

    public G_KillMonsters(GoalDifficulty difficulty, int stars) {
        super(difficulty, "Erledige Monster mit insgesamt " + stars + " Sternen");
        this.starsTarget = stars;
    }

    @Override
    public void earnedStars(Player player, int stars) {
        if (player == this.player) {
            this.stars += stars;
            check();
        }
    }

    @Override
    public void check() {
        if (this.stars >= starsTarget) {
            success();
        }
    }
}

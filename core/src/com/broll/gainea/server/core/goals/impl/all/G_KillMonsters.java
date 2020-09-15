package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.player.Player;

public class G_KillMonsters extends AbstractGoal {
    private final static int STARS = 7;
    private int stars;

    public G_KillMonsters() {
        super(GoalDifficulty.EASY, "Erledige Monster mit insgesamt " + STARS + " Sternen");
    }

    @Override
    public void earnedStars(Player player, int stars) {
        if (player == this.player) {
            this.stars += stars;
            if (this.stars >= STARS) {
                success();
            }
        }
    }
}

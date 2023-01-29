package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.goals.RoundGoal;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaCollection;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class G_OccupyRounds extends RoundGoal {

    private final static int ROUND_TARGET = 5;
    private AreaCollection container;

    public G_OccupyRounds() {
        super(GoalDifficulty.MEDIUM, "", ROUND_TARGET);
    }

    @Override
    public boolean init(GameContainer game, Player player) {
        container = RandomUtils.pickRandom(game.getMap().getAllAreas()).getContainer();
        this.text = "Sei für " + ROUND_TARGET + " Runden der einzige Spieler mit Einheiten auf " + container.getName();
        int containerSize = container.getAreas().size();
        if (containerSize < 5) {
            this.difficulty = GoalDifficulty.EASY;
        } else if (containerSize <= 10) {
            this.difficulty = GoalDifficulty.MEDIUM;
        } else {
            this.difficulty = GoalDifficulty.HARD;
        }
        return super.init(game, player);
    }

    @Override
    public void check() {
        List<Player> owners = container.getAreas().stream().flatMap(it -> it.getInhabitants().stream().map(object -> object.getOwner())).filter(it -> it != null).distinct().collect(Collectors.toList());
        if (owners.size() == 1 && owners.get(0) == player) {
            progressRound();
        } else {
            resetRounds();
        }
    }
}

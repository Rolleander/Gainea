package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.bot.strategy.GoalStrategy;
import com.broll.gainea.server.core.goals.CustomOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;
import com.google.common.collect.Sets;

public class G_StackUnits extends CustomOccupyGoal {
    private final static int COUNT = 6;
    private Area area;

    public G_StackUnits() {
        super(GoalDifficulty.EASY, null);
    }

    @Override
    public boolean init(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
        area = RandomUtils.pickRandom(game.getMap().getAllAreas());
        text = "Besetze " + area.getName() + " mit mindestens " + COUNT + " Einheiten";
        setExpansionRestriction(area.getContainer().getExpansion().getType());
        setProgressionGoal(COUNT);
        return true;
    }

    @Override
    public void check() {
        int playerUnits = (int) area.getInhabitants().stream().filter(it -> it.getOwner() == player && it instanceof Unit).count();
        updateProgression(playerUnits);
        if (playerUnits >= COUNT) {
            success();
        }
    }

    @Override
    public void botStrategy(GoalStrategy strategy) {
        strategy.setRequiredUnits(COUNT);
        strategy.updateTargets(Sets.newHashSet(area));
    }

}

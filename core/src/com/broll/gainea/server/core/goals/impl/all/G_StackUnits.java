package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.goals.OccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.LocationPicker;
import com.broll.gainea.server.core.player.Player;

public class G_StackUnits extends OccupyGoal {
    private final static int COUNT = 6;
    private Area area;

    public G_StackUnits() {
        super(GoalDifficulty.EASY, null);
    }

    @Override
    public boolean init(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
        area = LocationPicker.pickRandom(game.getMap(), 1).get(0);
        text = "Besetze " + area.getName() + " mit mindestens " + COUNT + " Einheiten";
        initOccupations();
        setExpansionRestriction(area.getContainer().getExpansion().getType());
        return true;
    }

    @Override
    protected void initOccupations() {
        condition(occupy(area), minPlayerUnitCount(COUNT));
    }

}

package com.broll.gainea.server.core.goals.impl.all;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.goals.AbstractOccupyGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.LocationPicker;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;

public class G_StackUnits extends AbstractOccupyGoal {
    private final static int COUNT = 6;
    private Area area;

    public G_StackUnits() {
        super(GoalDifficulty.EASY, null);
    }

    @Override
    public boolean init(GameContainer game, Player player) {
        area = LocationPicker.pickRandom(game.getMap(), 1).get(0);
        text = "Besetze " + area.getName() + " mit mindestens " + COUNT + " Einheiten";
        initOccupations();
        setExpansionRestriction(area.getContainer().getExpansion().getType());
        return true;
    }

    @Override
    protected void initOccupations() {
        condition(occupy(area), location -> location.getInhabitants().stream().filter(it -> {
            if (it instanceof BattleObject && ((BattleObject) it).getOwner() == player) {
                return true;
            }
            return false;
        }).count() >= COUNT);
    }

}

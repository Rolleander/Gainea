package com.broll.gainea.client.ui.components;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.ui.ingame.windows.GoalOverlay;
import com.broll.gainea.client.ui.utils.LabelUtils;
import com.broll.gainea.net.NT_Event_FinishedGoal;

public class FinishedGoalDisplay extends Table {

    public FinishedGoalDisplay(Gainea game, NT_Event_FinishedGoal goal, boolean myGoal) {
        String text = "";
        if (myGoal) {
            text = "Du hast ein Ziel erreicht!";
        } else {
            text = game.state.getPlayer(goal.player).name + " hat ein Ziel erreicht!";
        }
        setSkin(game.ui.skin);
        setBackground("highlight");
        pad(20, 10, 20, 10);
        defaults().space(30);
        add(LabelUtils.label(game.ui.skin, text)).center().row();
        add(GoalOverlay.renderGoal(game, goal.goal));
    }
}

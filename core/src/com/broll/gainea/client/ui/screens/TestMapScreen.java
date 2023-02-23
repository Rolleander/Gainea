package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.server.init.ExpansionSetting;

public class TestMapScreen extends Screen {

    public TestMapScreen() {
    }


    @Override
    public Actor build() {
//        ClientMapContainer.RENDER_DEBUG = true;
        game.state.init(ExpansionSetting.PLUS_ICELANDS, null);

        for (int i = 0; i < 3; i++) {
            NT_Goal goal = new NT_Goal();
            goal.points = 1 + i;
            goal.restriction = "Gainea";
            goal.description = "Erobere zweitausend wasauchimmer scahen und dir fehlt noch ziemlich viel";
            goal.progression = i;
            goal.progressionGoal = 5 * i;
            game.state.getGoals().add(goal);
        }
        game.ui.initInGameUi();
        game.ui.inGameUI.show();
        game.state.getMap().displayRenders();
        return new Table();
    }
}

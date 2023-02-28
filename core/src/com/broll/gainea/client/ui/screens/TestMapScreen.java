package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.client.ui.components.FinishedGoalDisplay;
import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Event_FinishedGoal;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.init.ExpansionSetting;

public class TestMapScreen extends Screen {

    public TestMapScreen() {
    }


    @Override
    public Actor build() {
//        ClientMapContainer.RENDER_DEBUG = true;
        game.state.init(ExpansionSetting.PLUS_ICELANDS, null);
        NT_Goal goal = null;

        for (int i = 0; i < 3; i++) {
            goal = new NT_Goal();
            goal.points = 1 + i;
            goal.restriction = "Gainea";
            goal.description = "Erobere zweitausend wasauchimmer scahen und dir fehlt noch ziemlich viel";
            goal.progression = i;
            goal.progressionGoal = 5 * i;
            goal.locations = new int[10];
            for (int h = 0; h < 10; h++) {
                goal.locations[h] = RandomUtils.random(0, 30);
            }
            game.state.getGoals().add(goal);
        }
        NT_BoardUpdate nt = new NT_BoardUpdate();
        nt.players = new NT_Player[2];
        for (int i = 0; i < 2; i++) {
            NT_Player p = new NT_Player();
            p.name = "tester " + i;
            p.cards = (byte) i;
            p.color = (byte) i;
            p.points = (byte) (i * 10);
            p.fraction = (byte) i;
            p.id = (short) i;
            p.stars = (short) (i * 5);
            p.units = new NT_Unit[0];
            nt.players[i] = p;
        }
        nt.objects = new NT_BoardObject[0];
        nt.effects = new NT_BoardEffect[0];
        nt.turns = 0;
        game.ui.initInGameUi();
        game.state.update(nt);
        game.ui.inGameUI.show();
        game.state.getMap().displayRenders();
        game.ui.inGameUI.updateWindows();
        NT_Event_FinishedGoal evt = new NT_Event_FinishedGoal();
        evt.goal = goal;
        FinishedGoalDisplay message = new FinishedGoalDisplay(game, evt, true);
        game.ui.inGameUI.showCenterOverlay(message);
        return new Table();
    }
}

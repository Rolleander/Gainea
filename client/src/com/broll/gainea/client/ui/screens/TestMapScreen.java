package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Event_FinishedGoal;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.init.ExpansionSetting;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestMapScreen extends Screen {
    private AtomicInteger idCounter = new AtomicInteger();

    public TestMapScreen() {
    }


    @Override
    public Actor build() {
        AudioPlayer.playSong("celtic.mp3");
        ClientMapContainer.RENDER_DEBUG = true;
        game.state.init(ExpansionSetting.FULL, 0, 30, null);
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
        nt.objects = new NT_BoardObject[1];
        nt.objects[0] = unit();
        nt.objects[0].description = "unit mit langer description die korrekt umgebrochen werden sollte ohne das ui zu verstopfen";
        // nt.objects[0].description = "unit mit kurzer description";
        nt.effects = new NT_BoardEffect[0];
        nt.round = 0;
        game.ui.initInGameUi();
        game.state.getMap().displayRenders();
        game.state.update(nt);
        game.ui.inGameUI.show();
        game.ui.inGameUI.updateWindows();
        NT_Event_FinishedGoal evt = new NT_Event_FinishedGoal();
        evt.goal = goal;
        //  FinishedGoalDisplay message = new FinishedGoalDisplay(game, evt, true);
        // game.ui.inGameUI.showCenterOverlay(message);
        game.ui.inGameUI.getLogWindow().logCardEvent("hat karte erhalten");
        game.ui.inGameUI.getLogWindow().logGoalEvent("hat goal erhalten, mit sehr langem text der unbedingt"
                + "umgebrochen werden sollte damit man noch etwas lesen kann im window....blablablabalbalbalbalabbla");

        Executors.newSingleThreadScheduledExecutor().schedule(() -> Gdx.app.postRunnable(this::lateInit), 300, TimeUnit.MILLISECONDS);

        return new Table();
    }

    private void lateInit() {
        short id = 0;
        for (int i = 0; i < 15; i++) {
            id = addCard();
        }
        game.state.turnIdle();
        List<NT_Action> actions = new ArrayList<>();
        NT_Action_Card ac = new NT_Action_Card();
        ac.cardId = id;
        actions.add(ac);
        game.state.performOptionalAction(actions, new PlayerPerformOptionalAction() {
            @Override
            public void none() {

            }

            @Override
            public void perform(NT_Action action, int option, int[] options) {

            }
        });
    }

    private NT_Unit unit() {
        NT_Monster u = new NT_Monster();
        u.stars = 4;
        u.id = (short) idCounter.incrementAndGet();
        u.icon = (short) MathUtils.random(0, 100);
        u.name = "Testfighter";
        u.power = (short) MathUtils.random(1, 3);
        u.health = (short) MathUtils.random(1, 5);
        u.maxHealth = u.health;
        u.location = (short) game.state.getMap().getArea(GaineaMap.Areas.WEIDESTEPPE).getNumber();
        return u;
    }

    private short addCard() {
        NT_Card c = new NT_Card();
        c.picture = (short) MathUtils.random(0, 80);
        c.text = "langer text für karte der noch sehr lang weitergehen kann und richtig umbrechen sollte und so weiter " +
                "umgebrochen werden sollte damit man noch etwas lesen kann im window....blablablabalbalbalbalabbla";
        c.title = "coole karte";
        c.id = (short) idCounter.incrementAndGet();
        game.state.getCards().add(c);
        game.ui.inGameUI.updateWindows();
        return c.id;
    }
}

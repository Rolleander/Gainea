package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.game.PlayerPerformOptionalAction;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.client.ui.ingame.hud.InfoMessageContainer;
import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Action_Shop;
import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Event_FinishedGoal;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Shop;
import com.broll.gainea.net.NT_ShopItem;
import com.broll.gainea.net.NT_ShopOther;
import com.broll.gainea.net.NT_ShopUnit;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.AreaID;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.networklib.client.impl.DummyLobbyPlayer;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
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
        game.state.init(ExpansionSetting.FULL, 0, 30, new DummyLobbyPlayer(0, null));

        NT_Shop shop = new NT_Shop();
        shop.items = new NT_ShopItem[8];
        for (int i = 0; i < shop.items.length; i++) {
            NT_ShopItem item;
            if (i <= 5) {
                item = new NT_ShopUnit();
                ((NT_ShopUnit) item).unit = unit();
                ((NT_ShopUnit) item).unit.description = "langer kauftext ok passt";
            } else {
                item = new NT_ShopOther();
                ((NT_ShopOther) item).description = "langer text für spell zum kaufen ok passt bestimmt";
            }
            item.price = (short) (5 + i);
            shop.items[i] = item;
        }
        game.state.shop = shop;

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
            if (i == 0) {
                p.units = new NT_Unit[10];
                for (int j = 0; j < 10; j++) {
                    p.units[j] = unit();
                    p.units[j].owner = 0;
                    p.units[j].location = getAreaNum(GaineaMap.Areas.GRUENLAND);
                }
                p.units[9].location = getAreaNum(GaineaMap.Areas.FELSWALD);
            }
            if (i == 1) {
                p.units = new NT_Unit[3];
                for (int j = 0; j < 3; j++) {
                    p.units[j] = unit();
                    p.units[j].owner = 1;
                    p.units[j].location = getAreaNum(GaineaMap.Areas.GROSSEWUESTE);
                }
            }
            nt.players[i] = p;
        }
        nt.objects = new NT_BoardObject[1];
        nt.objects[0] = unit();
        nt.objects[0].description = "unit mit langer description die korrekt umgebrochen werden sollte ohne das ui zu verstopfen";
        nt.effects = new NT_BoardEffect[0];
        nt.round = 0;
        game.state.update(nt);
        game.ui.initInGameUi();
        game.state.getMap().displayRenders();
        game.ui.inGameUI.show();
        game.ui.inGameUI.updateWindows();
        NT_Event_FinishedGoal evt = new NT_Event_FinishedGoal();
        evt.goal = goal;
        //  FinishedGoalDisplay message = new FinishedGoalDisplay(game, evt, true);
        // game.ui.inGameUI.showCenterOverlay(message);
        game.ui.inGameUI.getLogWindow().logCardEvent("hat karte erhalten");
        game.ui.inGameUI.getLogWindow().logGoalEvent("hat goal erhalten, mit sehr langem text der unbedingt"
                + "umgebrochen werden sollte damit man noch etwas lesen kann im window....blablablabalbalbalbalabbla");

        ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
        exec.schedule(() -> Gdx.app.postRunnable(this::lateInit), 300, TimeUnit.MILLISECONDS);
        exec.scheduleWithFixedDelay(() -> Gdx.app.postRunnable(this::infoMessage), 1, 1, TimeUnit.SECONDS);
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

        NT_Unit[] units = game.state.getPlayer(0).units.clone();
        units = ArrayUtils.remove(units, 0);
        NT_Action_Move move = new NT_Action_Move();
        move.units = units;
        move.location = getAreaNum(GaineaMap.Areas.XOMDELTA);
        actions.add(move);
        move = new NT_Action_Move();
        move.units = units;
        move.location = getAreaNum(GaineaMap.Areas.KUESTENGEBIET);
        actions.add(move);
        move = new NT_Action_Move();
        move.units = units;
        move.location = getAreaNum(GaineaMap.Areas.FELSWALD);
        actions.add(move);
        NT_Action_Attack atk = new NT_Action_Attack();
        atk.units = units;
        atk.location = getAreaNum(GaineaMap.Areas.WEIDESTEPPE);
        actions.add(atk);

        NT_Action_Shop buy = new NT_Action_Shop();
        buy.index = 5;
        actions.add(buy);

        game.state.performOptionalAction(actions, new PlayerPerformOptionalAction() {
            @Override
            public void none() {

            }

            @Override
            public void perform(NT_Action action, int option, int[] options) {

            }
        });

    }

    private void infoMessage() {
        InfoMessageContainer msgs = game.ui.inGameUI.infoMessages;
        String text = "test message: ";
        for (int i = 0; i < MathUtils.random(20); i++) {
            text += "-";
        }
        if (MathUtils.randomBoolean(0.5f)) {
            NT_Card c = new NT_Card();
            c.title = "Testkarte";
            c.picture = (short) MathUtils.random(80);
            msgs.showCardReceived(c);
        } else {
            msgs.show(text);
        }
    }

    private short getAreaNum(AreaID area) {
        return (short) game.state.getMap().getArea(area).getNumber();
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
        u.location = getAreaNum(GaineaMap.Areas.WEIDESTEPPE);
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

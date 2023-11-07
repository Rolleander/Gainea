package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.net.NT_Battle_Damage;
import com.broll.gainea.net.NT_Battle_Roll;
import com.broll.gainea.net.NT_BoardEffect;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_MercShop;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.init.ExpansionSetting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class TestBattleScreen extends Screen {

    private AtomicInteger idCounter = new AtomicInteger();

    public TestBattleScreen() {
    }

    @Override
    public Actor build() {
        ClientMapContainer.RENDER_DEBUG = true;
        game.state.init(ExpansionSetting.PLUS_ICELANDS, 0, 30, null);
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
        nt.round = 0;
        game.state.shop = new NT_MercShop();
        game.state.shop.units = new NT_Unit[0];
        game.ui.initInGameUi();
        game.state.update(nt);
        game.ui.inGameUI.show();
        game.state.getMap().displayRenders();
        game.ui.inGameUI.updateWindows();


        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

        executorService.scheduleAtFixedRate(this::startBattle, 0, 5, TimeUnit.SECONDS);

        return new Table();
    }


    private void startBattle() {
        List<NT_Unit> attackers = new ArrayList<>();
        List<NT_Unit> defenders = new ArrayList<>();

        attackers.add(battler());
        attackers.add(battler());
        defenders.add(battler());
        defenders.add(battler());

        game.ui.inGameUI.startBattle(attackers, defenders, game.state.getMap().getLocation(5), true);

        NT_Battle_Roll[] arolls = new NT_Battle_Roll[7];
        arolls[0] = roll(attackers.get(0));
        arolls[1] = roll(attackers.get(0));
        arolls[2] = roll(attackers.get(1));
        arolls[3] = roll(attackers.get(1));
        arolls[4] = roll(attackers.get(1));
        arolls[5] = roll(attackers.get(1));
        arolls[6] = roll(attackers.get(1));
        NT_Battle_Roll[] drolls = new NT_Battle_Roll[3];
        drolls[0] = roll(defenders.get(0));
        drolls[1] = roll(defenders.get(0));
        drolls[2] = roll(defenders.get(1));
        Arrays.sort(arolls, (a, b) -> b.number - a.number);
        Arrays.sort(drolls, (a, b) -> b.number - a.number);
        game.ui.inGameUI.updateBattle(arolls, drolls, damages(arolls, drolls), 0);
    }

    private Stack<NT_Battle_Damage> damages(NT_Battle_Roll[] arolls, NT_Battle_Roll[] drolls) {
        Stack<NT_Battle_Damage> damage = new Stack<>();
        for (int i = 0; i < Math.min(arolls.length, drolls.length); i++) {
            NT_Battle_Damage d = new NT_Battle_Damage();
            d.lethal = false;
            if (arolls[i].number > drolls[i].number) {
                d.source = arolls[i].sourceUnit;
                d.target = drolls[i].sourceUnit;
            } else {
                d.target = arolls[i].sourceUnit;
                d.source = drolls[i].sourceUnit;
            }
            damage.add(d);
        }
        return damage;
    }

    private NT_Battle_Roll roll(NT_Unit source) {
        NT_Battle_Roll r = new NT_Battle_Roll();
        r.number = MathUtils.random(r.min, r.max);
        r.sourceUnit = source.id;
        r.plus = MathUtils.random(-2, 2);
        return r;
    }

    private NT_Unit battler() {
        NT_Unit u = new NT_Unit();
        if (MathUtils.randomBoolean()) {
            u = new NT_Monster();
        }
        u.id = (short) idCounter.incrementAndGet();
        u.icon = (short) MathUtils.random(0, 100);
        u.name = "Testfighter ";
        u.power = (short) MathUtils.random(1, 3);
        u.health = (short) MathUtils.random(1, 5);
        u.maxHealth = u.health;
        return u;
    }
}

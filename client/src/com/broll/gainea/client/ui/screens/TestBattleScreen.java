package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.ui.Screen;
import com.broll.gainea.net.proto.*;

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
        game.state.initOffline(TestMapTopology.create(), 0, 30, 0);
        NT_BoardUpdate.Builder nt = NT_BoardUpdate.newBuilder();
        for (int i = 0; i < 2; i++) {
            nt.addPlayers(NT_Player.newBuilder().setName("tester " + i).setCards(i).setColor(i)
                .setPoints(i * 10).setFraction(i).setId(i).setStars(i * 5));
        }
        nt.setRound(0);
        game.state.shop = NT_Shop.getDefaultInstance();
        game.ui.initInGameUi();
        game.state.update(nt.build());
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

        List<NT_Battle_Roll> arolls = new ArrayList<>(Arrays.asList(roll(attackers.get(0)), roll(attackers.get(0)), roll(attackers.get(1)), roll(attackers.get(1)), roll(attackers.get(1)), roll(attackers.get(1)), roll(attackers.get(1))));
        List<NT_Battle_Roll> drolls = new ArrayList<>(Arrays.asList(roll(defenders.get(0)), roll(defenders.get(0)), roll(defenders.get(1))));
        arolls.sort((a, b) -> b.getNumber() - a.getNumber());
        drolls.sort((a, b) -> b.getNumber() - a.getNumber());
        game.ui.inGameUI.updateBattle(arolls, drolls, damages(arolls, drolls), 0);
    }

    private Stack<NT_Battle_Damage> damages(List<NT_Battle_Roll> arolls, List<NT_Battle_Roll> drolls) {
        Stack<NT_Battle_Damage> damage = new Stack<>();
        for (int i = 0; i < Math.min(arolls.size(), drolls.size()); i++) {
            NT_Battle_Damage.Builder d = NT_Battle_Damage.newBuilder().setLethal(false);
            if (arolls.get(i).getNumber() > drolls.get(i).getNumber()) {
                d.setSource(arolls.get(i).getSourceUnit()).setTarget(drolls.get(i).getSourceUnit());
            } else {
                d.setTarget(arolls.get(i).getSourceUnit()).setSource(drolls.get(i).getSourceUnit());
            }
            damage.add(d.build());
        }
        return damage;
    }

    private NT_Battle_Roll roll(NT_Unit source) {
        return NT_Battle_Roll.newBuilder().setNumber(MathUtils.random(1, 6)).setSourceUnit(source.getId())
            .setPlus(MathUtils.random(-2, 2)).build();
    }

    private NT_Unit battler() {
        int health = MathUtils.random(1, 5);
        NT_Unit.Builder unit = NT_Unit.newBuilder().setId(idCounter.incrementAndGet()).setIcon(MathUtils.random(0, 100))
            .setName("Testfighter ").setPower(MathUtils.random(1, 3)).setHealth(health).setMaxHealth(health);
        if (MathUtils.randomBoolean()) unit.setMonster(NT_Monster.newBuilder().setId(unit.getId()).setIcon(unit.getIcon())
            .setName(unit.getName()).setPower(unit.getPower()).setHealth(health).setMaxHealth(health));
        return unit.build();
    }
}

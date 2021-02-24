package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class GameUpdateReceiverProxy implements IGameUpdateReceiver {

    private Set<IGameUpdateReceiver> proxies = new HashSet<>();
    private int nestLevel;

    private List<Pair<IGameUpdateReceiver, Boolean>> updates = new ArrayList<>();

    public void register(IGameUpdateReceiver receiver) {
        updates.add(Pair.of(receiver, true));
        checkUpdates();
    }

    public void unregister(IGameUpdateReceiver receiver) {
        updates.add(Pair.of(receiver, false));
        checkUpdates();
    }

    private void runNested(Runnable run) {
        nestLevel++;
        run.run();
        nestLevel--;
        checkUpdates();
    }

    private void checkUpdates() {
        if (nestLevel == 0) {
            //do modifications
            performUpdates();
        }
    }

    private void performUpdates() {
        Iterator<Pair<IGameUpdateReceiver, Boolean>> iterator = updates.iterator();
        while (iterator.hasNext()) {
            Pair<IGameUpdateReceiver, Boolean> pair = iterator.next();
            if (pair.getRight()) {
                proxies.add(pair.getLeft());
            } else {
                proxies.remove(pair.getLeft());
            }
            iterator.remove();
        }
    }

    @Override
    public void battleResult(BattleResult result) {
        runNested(() -> proxies.forEach(proxy -> proxy.battleResult(result)));
    }

    @Override
    public void playedCard(AbstractCard card) {
        runNested(() -> proxies.forEach(proxy -> proxy.playedCard(card)));
    }

    @Override
    public void moved(List<MapObject> units, Location location) {
        runNested(() -> proxies.forEach(proxy -> proxy.moved(units, location)));
    }

    @Override
    public void spawned(MapObject object, Location location) {
        runNested(() -> proxies.forEach(proxy -> proxy.spawned(object, location)));
    }

    @Override
    public void damaged(BattleObject unit, int damage) {
        runNested(() -> proxies.forEach(proxy -> proxy.damaged(unit, damage)));
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        runNested(() -> proxies.forEach(proxy -> proxy.killed(unit, throughBattle)));
    }

    @Override
    public void earnedStars(Player player, int stars) {
        runNested(() -> proxies.forEach(proxy -> proxy.earnedStars(player, stars)));
    }

    @Override
    public void turnStarted(Player player) {
        runNested(() -> proxies.forEach(proxy -> proxy.turnStarted(player)));
    }

    @Override
    public void roundStarted() {
        runNested(() -> proxies.forEach(IGameUpdateReceiver::roundStarted));
    }
}

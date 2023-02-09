package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;

public class GameUpdateReceiverProxy implements IGameUpdateReceiver {

    private final static Logger Log = LoggerFactory.getLogger(GameUpdateReceiverProxy.class);
    private LinkedHashSet<IGameUpdateReceiver> receivers = new LinkedHashSet<>();
    private List<IGameUpdateReceiver> removed = new ArrayList<>();
    private int nestLevel;

    public void register(IGameUpdateReceiver receiver) {
        receivers.add(receiver);
        removed.remove(receiver);
        Log.trace("Receiver added to proxy [total: " + receivers.size() + "] (" + receiver + ")");
    }

    public void unregister(IGameUpdateReceiver receiver) {
        receivers.remove(receiver);
        removed.add(receiver);
        Log.trace("Receiver removed from proxy [total: " + receivers.size() + "] (" + receiver + ")");
    }

    private void run(Consumer<IGameUpdateReceiver> receiverCall) {
        nestLevel++;
        Iterator<IGameUpdateReceiver> iterator = new LinkedHashSet<>(receivers).iterator();
        while (iterator.hasNext()) {
            IGameUpdateReceiver receiver = iterator.next();
            if (isNotRemoved(receiver)) {
                receiverCall.accept(receiver);
            }
        }
        nestLevel--;
        if (nestLevel == 0) {
            removed.clear();
        }
    }

    private boolean isNotRemoved(IGameUpdateReceiver receiver) {
        return !removed.contains(receiver);
    }

    @Override
    public void battleResult(BattleResult result) {
        run(receiver -> receiver.battleResult(result));
    }

    @Override
    public void playedCard(Card card) {
        run(receiver -> receiver.playedCard(card));
    }

    @Override
    public void moved(List<MapObject> units, Location location) {
        run(receiver -> receiver.moved(units, location));
    }

    @Override
    public void spawned(MapObject object, Location location) {
        run(receiver -> receiver.spawned(object, location));
    }

    @Override
    public void damaged(BattleObject unit, int damage) {
        run(receiver -> receiver.damaged(unit, damage));
    }

    @Override
    public void killed(BattleObject unit, BattleResult throughBattle) {
        run(receiver -> receiver.killed(unit, throughBattle));
    }

    @Override
    public void earnedStars(Player player, int stars) {
        run(receiver -> receiver.earnedStars(player, stars));
    }

    @Override
    public void turnStarted(Player player) {
        run(receiver -> receiver.turnStarted(player));
    }

    @Override
    public void roundStarted() {
        run(IGameUpdateReceiver::roundStarted);
    }
}

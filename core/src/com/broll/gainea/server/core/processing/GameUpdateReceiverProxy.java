package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

import java.util.ArrayList;
import java.util.List;

public class GameUpdateReceiverProxy implements IGameUpdateReceiver {

    private List<IGameUpdateReceiver> proxies = new ArrayList<>();

    public void register(IGameUpdateReceiver receiver) {
        this.proxies.add(receiver);
    }

    public void unregister(IGameUpdateReceiver receiver) {
        this.proxies.remove(receiver);
    }

    @Override
    public void battleResult(BattleResult result) {
        proxies.forEach(proxy -> proxy.battleResult(result));
    }

    @Override
    public void playedCard(AbstractCard card) {
        proxies.forEach(proxy -> proxy.playedCard(card));
    }

    @Override
    public void moved(List<MapObject> units, Location location) {
        proxies.forEach(proxy -> proxy.moved(units, location));
    }

    @Override
    public void spawned(MapObject object, Location location) {
        proxies.forEach(proxy -> proxy.spawned(object, location));
    }

    @Override
    public void damaged(BattleObject unit, int damage) {
        proxies.forEach(proxy -> proxy.damaged(unit, damage));
    }

    @Override
    public void earnedStars(Player player, int stars) {
        proxies.forEach(proxy -> proxy.earnedStars(player, stars));
    }
}

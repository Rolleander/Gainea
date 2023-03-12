package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.RollManipulator;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;

import org.apache.commons.lang3.mutable.MutableBoolean;

import java.util.List;

public interface IGameUpdateReceiver {

    void battleIntention(BattleContext context, MutableBoolean cancelFight);

    void battleBegin(BattleContext context, RollManipulator rollManipulator);

    void battleResult(BattleResult result);

    void playedCard(Card card);

    void moved(List<MapObject> units, Location location);

    void spawned(MapObject object, Location location);

    void damaged(Unit unit, int damage);

    void killed(Unit unit, BattleResult throughBattle);

    void earnedStars(Player player, int stars);

    void turnStarted(Player player);

    void roundStarted();

}

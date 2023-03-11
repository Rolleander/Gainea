package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.RollManipulator;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;

import java.util.List;

public abstract class GameUpdateReceiverAdapter implements IGameUpdateReceiver {

    @Override
    public void battleBegin(BattleContext context, RollManipulator rollManipulator) {

    }

    @Override
    public void battleResult(BattleResult result) {

    }

    @Override
    public void playedCard(Card card) {

    }

    @Override
    public void moved(List<MapObject> units, Location location) {

    }

    @Override
    public void spawned(MapObject object, Location location) {

    }

    @Override
    public void damaged(Unit unit, int damage) {

    }

    @Override
    public void earnedStars(Player player, int stars) {

    }

    @Override
    public void turnStarted(Player player) {

    }

    @Override
    public void roundStarted() {

    }

    @Override
    public void killed(Unit unit, BattleResult throughBattle) {

    }
}

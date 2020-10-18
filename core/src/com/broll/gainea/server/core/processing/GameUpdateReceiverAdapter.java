package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

import java.util.List;

public abstract class GameUpdateReceiverAdapter implements IGameUpdateReceiver {
    @Override
    public void battleResult(BattleResult result) {

    }

    @Override
    public void playedCard(AbstractCard card) {

    }

    @Override
    public void moved(List<MapObject> units, Location location) {

    }

    @Override
    public void spawned(MapObject object, Location location) {

    }

    @Override
    public void damaged(BattleObject unit, int damage) {

    }

    @Override
    public void earnedStars(Player player, int stars) {

    }
}

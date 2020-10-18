package com.broll.gainea.server.core.processing;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightResult;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.player.Player;

import java.util.List;

public interface IGameUpdateReceiver {

    void battleResult(BattleResult result);

    void playedCard(AbstractCard card);

    void moved(List<MapObject> units, Location location);

    void spawned(MapObject object, Location location);

    void damaged(BattleObject unit, int damage);

    void earnedStars(Player player, int stars);
}

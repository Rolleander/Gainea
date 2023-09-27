package com.broll.gainea.server.core.processing

import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.RollManipulator
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import org.apache.commons.lang3.mutable.MutableBoolean

abstract class GameUpdateReceiverAdapter : IGameUpdateReceiver {
    override fun battleIntention(context: BattleContext?, cancelFight: MutableBoolean) {}
    override fun battleBegin(context: BattleContext?, rollManipulator: RollManipulator?) {}
    override fun battleResult(result: BattleResult) {}
    override fun playedCard(card: Card?) {}
    override fun moved(units: List<MapObject?>?, location: Location?) {}
    override fun spawned(`object`: MapObject, location: Location) {}
    override fun damaged(unit: Unit?, damage: Int) {}
    override fun earnedStars(player: Player, stars: Int) {}
    override fun turnStarted(player: Player) {}
    override fun roundStarted() {}
    override fun killed(unit: Unit?, throughBattle: BattleResult?) {}
}

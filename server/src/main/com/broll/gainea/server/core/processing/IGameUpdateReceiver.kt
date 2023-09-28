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

interface IGameUpdateReceiver {
    fun battleIntention(context: BattleContext, cancelFight: MutableBoolean)
    fun battleBegin(context: BattleContext, rollManipulator: RollManipulator)
    fun battleResult(result: BattleResult)
    fun playedCard(card: Card)
    fun moved(units: List<MapObject>, location: Location)
    fun spawned(`object`: MapObject, location: Location)
    fun damaged(unit: Unit, damage: Int)
    fun killed(unit: Unit, throughBattle: BattleResult?)
    fun earnedStars(player: Player, stars: Int)
    fun turnStarted(player: Player)
    fun roundStarted()
}

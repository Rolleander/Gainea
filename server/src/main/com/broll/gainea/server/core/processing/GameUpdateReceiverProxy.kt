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
import org.slf4j.LoggerFactory

class GameUpdateReceiverProxy : IGameUpdateReceiver {
    private val receivers = LinkedHashSet<IGameUpdateReceiver>()
    private val removed = mutableListOf<IGameUpdateReceiver>()
    private var nestLevel = 0
    fun register(receiver: IGameUpdateReceiver) {
        receivers.add(receiver)
        removed.remove(receiver)
        Log.trace("Receiver added to proxy [total: " + receivers.size + "] (" + receiver + ")")
    }

    fun unregister(receiver: IGameUpdateReceiver) {
        receivers.remove(receiver)
        removed.add(receiver)
        Log.trace("Receiver removed from proxy [total: " + receivers.size + "] (" + receiver + ")")
    }

    private fun run(receiverCall: (IGameUpdateReceiver) -> kotlin.Unit) {
        nestLevel++
        val iterator = LinkedHashSet(receivers).iterator()
        while (iterator.hasNext()) {
            val receiver = iterator.next()
            if (isNotRemoved(receiver)) {
                receiverCall(receiver)
            }
        }
        nestLevel--
        if (nestLevel == 0) {
            removed.clear()
        }
    }

    private fun isNotRemoved(receiver: IGameUpdateReceiver?): Boolean {
        return !removed.contains(receiver)
    }

    override fun battleIntention(context: BattleContext, cancelFight: MutableBoolean) =
            run { it.battleIntention(context, cancelFight) }


    override fun battleBegin(context: BattleContext, rollManipulator: RollManipulator) =
            run { it.battleBegin(context, rollManipulator) }

    override fun battleResult(result: BattleResult) =
            run { it.battleResult(result) }

    override fun playedCard(card: Card) = run { it.playedCard(card) }

    override fun moved(units: List<MapObject>, location: Location) = run { it.moved(units, location) }

    override fun spawned(mapObject: MapObject, location: Location) = run { it.spawned(mapObject, location) }

    override fun damaged(unit: Unit, damage: Int) = run { it.damaged(unit, damage) }

    override fun killed(unit: Unit, throughBattle: BattleResult?) = run { it.killed(unit, throughBattle) }

    override fun earnedStars(player: Player, stars: Int) = run { it.earnedStars(player, stars) }

    override fun turnStarted(player: Player) = run { it.turnStarted(player) }

    override fun roundStarted() = run { it.roundStarted() }

    companion object {
        private val Log = LoggerFactory.getLogger(GameUpdateReceiverProxy::class.java)
    }
}

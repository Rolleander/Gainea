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
import java.util.function.Consumer

class GameUpdateReceiverProxy : IGameUpdateReceiver {
    private val receivers = LinkedHashSet<IGameUpdateReceiver?>()
    private val removed: MutableList<IGameUpdateReceiver?> = ArrayList()
    private var nestLevel = 0
    fun register(receiver: IGameUpdateReceiver?) {
        receivers.add(receiver)
        removed.remove(receiver)
        Log.trace("Receiver added to proxy [total: " + receivers.size + "] (" + receiver + ")")
    }

    fun unregister(receiver: IGameUpdateReceiver?) {
        receivers.remove(receiver)
        removed.add(receiver)
        Log.trace("Receiver removed from proxy [total: " + receivers.size + "] (" + receiver + ")")
    }

    private fun run(receiverCall: Consumer<IGameUpdateReceiver?>) {
        nestLevel++
        val iterator: Iterator<IGameUpdateReceiver?> = LinkedHashSet(receivers).iterator()
        while (iterator.hasNext()) {
            val receiver = iterator.next()
            if (isNotRemoved(receiver)) {
                receiverCall.accept(receiver)
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

    override fun battleIntention(context: BattleContext?, cancelFight: MutableBoolean) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.battleIntention(context, cancelFight) }
    }

    override fun battleBegin(context: BattleContext?, rollManipulator: RollManipulator?) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.battleBegin(context, rollManipulator) }
    }

    override fun battleResult(result: BattleResult) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.battleResult(result) }
    }

    override fun playedCard(card: Card?) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.playedCard(card) }
    }

    override fun moved(units: List<MapObject?>?, location: Location?) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.moved(units, location) }
    }

    override fun spawned(`object`: MapObject, location: Location) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.spawned(`object`, location) }
    }

    override fun damaged(unit: Unit?, damage: Int) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.damaged(unit, damage) }
    }

    override fun killed(unit: Unit?, throughBattle: BattleResult?) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.killed(unit, throughBattle) }
    }

    override fun earnedStars(player: Player, stars: Int) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.earnedStars(player, stars) }
    }

    override fun turnStarted(player: Player) {
        run { receiver: IGameUpdateReceiver? -> receiver!!.turnStarted(player) }
    }

    override fun roundStarted() {
        run { obj: IGameUpdateReceiver? -> obj!!.roundStarted() }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(GameUpdateReceiverProxy::class.java)
    }
}

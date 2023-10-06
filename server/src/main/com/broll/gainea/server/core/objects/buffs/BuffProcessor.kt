package com.broll.gainea.server.core.objects.buffs

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.UnitControl.kill
import com.broll.gainea.server.core.utils.UnitControl.update
import org.apache.commons.collections4.map.MultiValueMap

class BuffProcessor(private val game: Game) : GameUpdateReceiverAdapter() {
    private var currentTurnCount = 0
    private val timedBuffs = MultiValueMap<Int, Buff<*>>()
    private val gloabalBuffs: MutableMap<Buff<*>, GlobalBuff> = HashMap()

    init {
        game.updateReceiver.register(this)
    }

    override fun turnStarted(player: Player) {
        currentTurnCount++
        val timedout = timedBuffs.getCollection(currentTurnCount)
        if (timedout != null) {
            timedout.forEach { timedout(it) }
            timedBuffs.remove(currentTurnCount)
        }
    }

    private fun timedout(buff: Buff<*>) {
        val affectedObjects = ArrayList(buff.affectedObjects)
        buff.remove()
        gloabalBuffs.remove(buff)
        //check if any unit died because of removal of buffs
        val update = mutableListOf<MapObject>()
        for (target in affectedObjects) {
            if (target is Unit) {
                if (target.dead) {
                    game.kill(target)
                } else {
                    update.add(target)
                }
            }
        }
        game.update(update)
    }

    fun timeoutBuff(buff: Buff<*>, rounds: Int) {
        val timeoutTurn = currentTurnCount + game.allPlayers.size * Math.max(1, rounds)
        timedBuffs[timeoutTurn] = buff
    }

    fun addGlobalBuff(globalBuff: GlobalBuff, effect: Int) {
        gloabalBuffs[globalBuff.buff] = globalBuff
        //apply directly to all existing units for the targets
        globalBuff.targets.forEach { player ->
            val units = if (player.isNeutral()) {
                game.objects.filterIsInstance<Unit>()
            } else {
                player.units
            }
            units.forEach { globalBuff.apply(it) }
            game.update(units)
        }
    }

    fun applyGlobalBuffs(unit: Unit) =
            gloabalBuffs.values.filter { it.targets.contains(unit.owner) }.forEach {
                it.apply(unit)
            }

}

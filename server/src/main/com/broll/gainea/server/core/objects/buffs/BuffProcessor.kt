package com.broll.gainea.server.core.objects.buffs

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.processing.TurnDuration
import com.broll.gainea.server.core.utils.UnitControl.kill
import com.broll.gainea.server.core.utils.UnitControl.update

class BuffProcessor(private val game: Game) : GameUpdateReceiverAdapter() {

    private val timedBuffs = mutableListOf<Pair<TurnDuration, Buff<*>>>()
    private val globalBuffs: MutableMap<Buff<*>, GlobalBuff> = HashMap()

    init {
        game.updateReceiver.register(this)
    }

    override fun turnStarted(player: Player) {
        val iterator = timedBuffs.iterator()
        while (iterator.hasNext()) {
            val timedBuff = iterator.next()
            timedBuff.first.progress()
            if (timedBuff.first.completed()) {
                timedout(timedBuff.second)
                iterator.remove()
            }
        }
    }

    private fun timedout(buff: Buff<*>) {
        val affectedObjects = ArrayList(buff.affectedObjects)
        buff.remove()
        globalBuffs.remove(buff)
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

    fun timeoutBuff(buff: Buff<*>, duration: TurnDuration) {
        timedBuffs += duration to buff
        duration.register(game)
    }

    fun addGlobalBuff(globalBuff: GlobalBuff, effect: Int) {
        globalBuffs[globalBuff.buff] = globalBuff
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
        globalBuffs.values.filter { it.targets.contains(unit.owner) }.forEach {
            it.apply(unit)
        }

}

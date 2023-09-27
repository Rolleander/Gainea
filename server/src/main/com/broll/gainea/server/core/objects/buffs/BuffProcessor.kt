package com.broll.gainea.server.core.objects.buffs

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.UnitControl
import org.apache.commons.collections4.map.MultiValueMap
import java.util.function.Consumer
import java.util.stream.Collectors

class BuffProcessor(private val game: GameContainer) : GameUpdateReceiverAdapter() {
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
        val update= mutableListOf<MapObject>()
        for (target in affectedObjects) {
            if (target is Unit) {
                if (target.isDead) {
                    UnitControl.kill(game, target)
                } else {
                    update.add(target)
                }
            }
        }
        UnitControl.update(game, update)
    }

    fun timeoutBuff(buff: Buff<*>, rounds: Int) {
        val timeoutTurn = currentTurnCount + game.allPlayers.size * Math.max(1, rounds)
        timedBuffs[timeoutTurn] = buff
    }

    fun addGlobalBuff(globalBuff: GlobalBuff, effect: Int) {
        gloabalBuffs[globalBuff.buff] = globalBuff
        //apply directly to all existing units for the targets
        globalBuff.targets.forEach {player->
           val units = game.objects.filterIsInstance(Unit::class.java).filter { it.owner==player }
            units.forEach{globalBuff.apply(it)}
            UnitControl.update(game, units)
        }
        if(globalBuff.forNeutral){
            val units =  game.objects.filterIsInstance(Unit::class.java).filterNot { it.owner==null }
            units.forEach{globalBuff.apply(it)}
            UnitControl.update(game, units)
        }
    }

    fun applyGlobalBuffs(unit: Unit) =
        gloabalBuffs.values.filter { it.targets.contains(unit.owner) || (it.forNeutral && unit.owner==null) }.forEach {
            it.apply(unit)
        }

}

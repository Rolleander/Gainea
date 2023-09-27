package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_Eventimport

com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.buffs.BuffTypeimport com.broll.gainea.server.core.objects.buffs.IntBuffimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.UnitControlimport java.util.function.Consumer
class C_Rooting : Card(63, "Schattenfesseln", "Wählt eine feindliche Truppe. Diese kann sich für " + DURATION + " Runden nicht bewegen.") {
    init {
        drawChance = 0.7f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val location = selectHandler!!.selectLocation("Ziel für Schattenfesseln wählen", ArrayList(PlayerUtils.getHostileLocations(game!!, owner)))
        val units: List<MapObject?> = ArrayList(location.inhabitants)
        val rootDebuff = IntBuff(BuffType.SET, 0)
        units.forEach(Consumer { unit: MapObject? ->
            unit!!.movesPerTurn!!.addBuff(rootDebuff)
            if (unit is Unit) {
                unit.attacksPerTurn.addBuff(rootDebuff)
            }
        })
        UnitControl.focus(game, units, NT_Event.EFFECT_DEBUFF)
        game.buffProcessor.timeoutBuff(rootDebuff, DURATION)
    }

    companion object {
        private const val DURATION = 2
    }
}

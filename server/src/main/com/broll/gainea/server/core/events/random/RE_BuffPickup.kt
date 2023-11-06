package com.broll.gainea.server.core.events.random

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeArea
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.objects.buffs.BuffType.ADD
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.utils.UnitControl.focus
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.selectUnit

class RE_BuffPickup : RandomEvent() {
    override fun pickSpot() = game.freeArea()

    override fun run() {
        val obj = Collectible(game)
        obj.onPickup = { player ->
            val unit = game.selectUnit(
                player,
                "Welche Einheit soll verstärkt werden?",
                obj.location.units.filter { it.owner == player })!!
            val buff = IntBuff(ADD, 3)
            unit.addHealthBuff(buff)
            unit.power.addBuff(buff)
            game.focus(unit, NT_Event.EFFECT_BUFF)
        }
        obj.name = "Waffenschmied"
        obj.description = "Eroberer verleiht einer Einheit +3/+3"
        obj.icon = 132
        game.spawn(obj, location)
    }


}
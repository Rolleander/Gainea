package com.broll.gainea.server.core.cards.events.random

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.RandomEvent
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.objects.buffs.BuffType.ADD
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.selectUnit

class RE_BuffPickup : RandomEvent() {
    override fun run(game: Game) {
        game.freeArea { area ->
            val obj = Collectible(game)
            obj.onPickup = { player ->
                val unit = game.selectUnit(player, "Welche Einheit soll verstärkt werden?", obj.location.units.filter { it.owner == player })!!
                val buff = IntBuff(ADD, 3)
                unit.addHealthBuff(buff)
                unit.power.addBuff(buff)
                //todo update
            }
            obj.name = "Waffenschmied"
            obj.description = "Eroberer verleiht einer Einheit +3/+3"
            obj.icon = 132
            game.spawn(obj, area)
        }
    }

}
package com.broll.gainea.server.core.events.random

import com.broll.gainea.net.NT_Event.EFFECT_BUFF
import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeBuildingSpot
import com.broll.gainea.server.core.objects.Conquerable
import com.broll.gainea.server.core.utils.UnitControl.focus
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_Castle : RandomEvent() {

    override fun pickSpot() = game.freeBuildingSpot(onlyContinents = true)
    override fun run() {
        val obj = Conquerable(game, despawn = false)
        obj.afterConquer = { player ->
            val unit = obj.location.units.random()
            unit.power.addValue(1)
            game.focus(unit, EFFECT_BUFF)
        }
        obj.name = "Burg"
        obj.description =
            "Verleiht jede Runde einem Besetzer +1 Angriff"
        obj.icon = 2
        game.spawn(obj, location)
    }

}
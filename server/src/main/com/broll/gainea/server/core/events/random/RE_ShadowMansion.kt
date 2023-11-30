package com.broll.gainea.server.core.events.random

import com.broll.gainea.server.core.events.RandomEvent
import com.broll.gainea.server.core.events.freeBuildingSpot
import com.broll.gainea.server.core.objects.Conquerable
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterActivity.ALWAYS
import com.broll.gainea.server.core.objects.monster.MonsterBehavior.RANDOM
import com.broll.gainea.server.core.utils.UnitControl.spawn

class RE_ShadowMansion : RandomEvent() {

    override fun pickSpot() = game.freeBuildingSpot()
    override fun run() {
        val obj = Conquerable(game)
        obj.whenRoundsHold = { player ->
            val skeleton = Monster(player)
            skeleton.setStats(1, 1)
            skeleton.icon = 89
            skeleton.behavior = RANDOM
            skeleton.activity = ALWAYS
            skeleton.controllable = false
            skeleton.name = "Skelett"
            game.spawn(skeleton, obj.location)
        }
        obj.name = "Nekropolis"
        obj.description =
            "Beschwört jede Runde ein unkontrollierbares Skelett für den Besetzer"
        obj.icon = 4
        game.spawn(obj, location)
    }

}
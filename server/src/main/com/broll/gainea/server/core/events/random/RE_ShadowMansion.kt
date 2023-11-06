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
        val obj = Conquerable(game, despawn = false)
        obj.afterConquer = { player ->
            val skeleton = Monster(player)
            skeleton.setStats(1, 1)
            skeleton.icon = 89
            skeleton.activity = ALWAYS
            skeleton.behavior = RANDOM
            skeleton.controllable = false
            skeleton.name = "Skelett"
            game.spawn(skeleton, obj.location)
        }
        obj.name = "Schatten"
        obj.description =
            "Beschwört jede Runde ein unkontrollierbares Skelett für den Besetzer"
        obj.icon = 4
        game.spawn(obj, location)
    }

}
package com.broll.gainea.server.core.objects.impl

import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterActivity.OFTEN
import com.broll.gainea.server.core.objects.monster.MonsterBehavior.AGGRESSIVE
import com.broll.gainea.server.core.player.Player

class DemonKnight(owner: Player) : Monster(owner) {

    init {
        icon = 93
        controllable = false
        behavior = AGGRESSIVE
        activity = OFTEN
        name = "Dämonenkrieger"
        description = "Unkontrollierbar"
        setStats(4, 4)
    }

}
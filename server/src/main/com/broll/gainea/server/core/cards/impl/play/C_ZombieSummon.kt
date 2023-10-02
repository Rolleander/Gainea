package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterActivity
import com.broll.gainea.server.core.objects.monster.MonsterBehavior
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_ZombieSummon : Card(78, "Rückkehr der Verdammten", "Ruft für die Anzahl an Kills eurer aktuellen Einheiten" +
        " jeweils einen Zombie herbei, der nicht kontrolliert werden kann.") {
    private val kills: Int
        get() = owner.units.sumOf { it.kills }
    override val isPlayable: Boolean
        get() = kills > 0 && owner.controlledLocations.isNotEmpty()

    override fun play() {
        owner.units.toList().forEach { unit ->
            for (i in 0 until unit.kills) {
                val zombie = Zombie()
                zombie.owner = owner
                game.spawn(zombie, unit.location)
            }
        }
    }

    private inner class Zombie : Monster(game.neutralPlayer) {
        init {
            controllable = false
            name = "Verdammter"
            behavior = MonsterBehavior.RANDOM
            activity = MonsterActivity.OFTEN
            setStats(1, 1)
            icon = 128
        }
    }
}

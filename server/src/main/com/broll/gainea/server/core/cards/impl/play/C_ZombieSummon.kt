package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.objects.monster.MonsterActivityimport com.broll.gainea.server.core.objects.monster.MonsterBehaviorimport com.broll.gainea.server.core.utils.StreamUtils
class C_ZombieSummon : Card(78, "Rückkehr der Verdammten", "Ruft für die Anzahl an Kills eurer aktuellen Einheiten" +
        " jeweils einen Zombie herbei, der nicht kontrolliert werden kann.") {
    private val kills: Int
        private get() = owner.units.stream().mapToInt { it: Unit? -> it.getKills() }.sum()
    override val isPlayable: Boolean
        get() = kills > 0 && !owner.controlledLocations.isEmpty()

    override fun play() {
        StreamUtils.safeForEach<Unit?>(owner.units.stream()) { unit: Unit? ->
            for (i in 0 until unit.getKills()) {
                val zombie = Zombie()
                zombie.owner = owner
                spawn(game, zombie, unit.getLocation())
            }
        }
    }

    private inner class Zombie : Monster() {
        init {
            controllable = false
            name = "Verdammter"
            setBehavior(MonsterBehavior.RANDOM)
            setActivity(MonsterActivity.OFTEN)
            setStats(1, 1)
            icon = 128
        }
    }
}

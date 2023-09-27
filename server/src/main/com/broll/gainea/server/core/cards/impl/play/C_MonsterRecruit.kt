package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.monster.GodDragonimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.utils.SelectionUtilsimport com.broll.gainea.server.core.utils.UnitControlimport java.util.function.Consumer
class C_MonsterRecruit : Card(71, "Tierfreund", "Rekrutiert ein benachbartes Monster (ausser Götterdrache)") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = !monsterLocations.isEmpty()

    private fun validMonster(`object`: MapObject?): Boolean {
        return `object` is Monster && `object`.getOwner() == null && `object` !is GodDragon
    }

    private val monsterLocations: Collection<Location?>
        private get() {
            val monsterLocations: MutableSet<Location?> = HashSet()
            owner.controlledLocations.forEach(Consumer { location: Location? ->
                location.getConnectedLocations().stream().filter { it: Location? -> it.getInhabitants().stream().anyMatch { `object`: MapObject? -> validMonster(`object`) } }
                        .forEach { e: Location? -> monsterLocations.add(e) }
            }
            )
            return monsterLocations
        }

    override fun play() {
        val monsterLocations = monsterLocations
        if (!monsterLocations.isEmpty()) {
            val monster = SelectionUtils.selectUnitFromLocations(game!!,
                    monsterLocations, { `object`: Unit? -> validMonster(`object`) },
                    "Welches Monster soll rekrutiert werden?") as Monster
            owner.units.add(monster)
            monster.removeActionTimer()
            monster.owner = owner
            game.objects.remove(monster)
            UnitControl.focus(game, monster, 0)
        }
    }
}

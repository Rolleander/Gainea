package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.utils.UnitControlimport com.google.common.collect.Listsimport java.util.Collectionsimport java.util.stream.Collectors
class C_Panic : Card(46, "Massenpanik", "Wählt ein Land mit mindestens einer Einheit. Alle Einheiten des gewählten Ortes werden auf angrenzende Orte verteilt.") {
    init {
        drawChance = 0.9f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val locations = game.map.allAreas.stream().filter { it: Area? -> !it.getInhabitants().isEmpty() }.collect(Collectors.toList())
        if (!locations.isEmpty()) {
            val source = selectHandler!!.selectLocation("Wählt einen Zielort für die Panik", locations)
            val inhabitants: List<MapObject?> = ArrayList(source.inhabitants)
            val neighbours: List<Location?> = ArrayList(source.connectedLocations)
            Collections.shuffle(neighbours)
            var index = 0
            for (`object` in inhabitants) {
                val target = neighbours[index]
                UnitControl.move(game!!, Lists.newArrayList(`object`), target)
                index++
                if (index >= neighbours.size) {
                    index = 0
                }
            }
        }
    }
}

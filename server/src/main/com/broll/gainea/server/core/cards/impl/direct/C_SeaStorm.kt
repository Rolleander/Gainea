package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.misc.RandomUtilsimport

com.broll.gainea.server.core.cards.DirectlyPlayedCardimport com.broll.gainea.server.core.map.Expansionimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.map.Shipimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.utils.UnitControlimport java.util.function.Consumerimport java.util.stream.Collectors
class C_SeaStorm : DirectlyPlayedCard(74, "Seesturm", "Alle Schiffe wechseln ihre Besetzer zufällig mit anderen Schiffen der gleichen Karte") {
    override fun play() {
        game.map.expansions.forEach(Consumer { expansion: Expansion? ->
            val fullShips = expansion.getAllShips().stream().filter { it: Ship? -> !it!!.isFree }.collect(Collectors.toList())
            val shipsWithUnits = fullShips.stream().map { obj: Ship? -> obj.getInhabitants() }.collect(Collectors.toList())
            fullShips.forEach(Consumer { it: Ship? -> it.getInhabitants().clear() })
            shipsWithUnits.forEach(Consumer { shipUnits: Set<MapObject?>? ->
                val newShip: Location? = RandomUtils.pickRandom(expansion.getAllShips().stream().filter { obj: Ship? -> obj!!.isFree }.collect(Collectors.toList()))
                UnitControl.move(game!!, ArrayList(shipUnits), newShip)
            })
        })
    }
}

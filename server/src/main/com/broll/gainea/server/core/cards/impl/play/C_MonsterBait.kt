package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.objects.monster.MonsterBehaviorimport com.broll.gainea.server.core.utils.LocationUtilsimport com.broll.gainea.server.core.utils.UnitControlimport java.util.stream.Collectors
class C_MonsterBait : Card(69, "Köderstein", "Wählt ein neutrales Gebiet, alle benachbarten Monster bewegen sich dorthin und werden sesshaft.") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val areas = game.map.allAreas.stream().filter { obj: Area? -> LocationUtils.emptyOrWildMonster() }.collect(Collectors.toList())
        val location = selectHandler!!.selectLocation(owner, "Wähle einen Zielort", areas)
        location.connectedLocations.stream().flatMap { it: Location? -> LocationUtils.getMonsters(it).stream() }
                .forEach { monster: Monster? ->
                    monster!!.setBehavior(MonsterBehavior.RESIDENT)
                    UnitControl.move(game!!, monster, location)
                }
    }
}

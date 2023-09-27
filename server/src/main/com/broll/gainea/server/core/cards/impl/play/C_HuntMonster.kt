package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.utils.LocationUtilsimport com.broll.gainea.server.core.utils.PlayerUtilsimport java.util.stream.Collectors
class C_HuntMonster : Card(37, "Drachenjagd", "Wählt eine Truppe und greift damit ein beliebiges Monster auf der gleichen Karte an") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val from = selectHandler!!.selectLocation("Wählt eure Angriffstruppe", owner.controlledLocations)
        val attackers = PlayerUtils.getUnits(owner, from)
        val attackLocations = LocationUtils.getWildMonsterLocations(game!!).stream()
                .filter { it: Location? -> it.getContainer().expansion === from.container.expansion }.collect(Collectors.toList())
        if (!attackLocations.isEmpty()) {
            val target = selectHandler!!.selectLocation("Wählt den Angriffsort aus", attackLocations)
            game.battleHandler.startBattle(attackers, LocationUtils.getMonsters(target))
        }
    }
}

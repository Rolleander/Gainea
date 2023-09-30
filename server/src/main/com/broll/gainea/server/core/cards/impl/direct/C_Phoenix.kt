package com.broll.gainea.server.core.cards.impl.direct

import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterMotion

class C_Phoenix : DirectlyPlayedCard(65, "Beschworener Phönix", "Beschwört einen Phönix (2/2), dieser kann 2 Felder pro Zug bewegt werden aber kann nicht angreifen.") {
    init {
        drawChance = 0.8f
    }

    override fun play() {
        val monster = Monster(game.neutralPlayer)
        monster.name = "Phönix"
        monster.attacksPerTurn.value = 0
        monster.movesPerTurn.value = 2
        monster.setHealth(2)
        monster.setPower(2)
        monster.icon = 124
        monster.motion = MonsterMotion.AIRBORNE
        monster.owner = owner
        placeUnitHandler.placeUnit(owner, monster, owner.controlledLocations, "Ort der Beschwörung wählen")
    }
}

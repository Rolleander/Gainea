package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.SUMMON
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getEnemyLocations
import com.broll.gainea.server.core.utils.getHostileUnits

class C_Assassin : Card(
    4, SUMMON, "Attentat",
    "Wählt eine feindliche Armee und rekrutiert einen Assassinen (4/2), der diese direkt angreift."
) {


    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = game.getEnemyLocations(owner).isNotEmpty()

    override fun play() {
        val location = selectHandler.selectLocation(
            "Wo soll der Assassine angreifen?",
            game.getEnemyLocations(owner)
        )
        val assassin = Assassin()
        game.spawn(assassin, location)
        game.battleHandler.startBattle(listOf(assassin), location.getHostileUnits(owner), false)
    }

    private inner class Assassin : Soldier(owner) {
        init {
            fraction = null
            name = "Assassine"
            setStats(4, 2)
            icon = 129
        }
    }
}

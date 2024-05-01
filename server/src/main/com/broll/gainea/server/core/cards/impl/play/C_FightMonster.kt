package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.SUMMON
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.selectWildMonster

class C_FightMonster : Card(
    83, SUMMON, "Drachenjäger",
    "Wählt ein wildes Monster und rekrutiert einen Drachenjäger (4/3) der dieses ohne Rückzug angreift (Ihr erhaltet keine Kampf-Belohnungen)"
) {

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        game.selectWildMonster("Wählt ein Monster")?.let { monster ->
            val soldier = Soldier(owner)
            soldier.setStats(4, 3)
            soldier.icon = 3
            soldier.name = "Drachenjäger"
            game.spawn(soldier, monster.location)
            game.battleHandler.startBattle(
                listOf(soldier),
                listOf(monster),
                allowRetreat = false,
                grantRewards = false
            )
        }

    }
}
package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.utils.UnitControl.kill
import com.broll.gainea.server.core.utils.isCommander
import com.broll.gainea.server.core.utils.selectOtherPlayersUnit

class C_KillSoldier : Card(
    34,
    DISRUPTION,
    "Scharfschütze",
    "Tötet eine beliebige feindliche Einheit (Außer Feldherr)"
) {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val unit = game.selectOtherPlayersUnit(
            owner,
            "Wählt eine Einheit aus die vernichtet werden soll"
        ) { !it.isCommander() }
        if (unit != null) {
            game.kill(unit)
        }
    }
}

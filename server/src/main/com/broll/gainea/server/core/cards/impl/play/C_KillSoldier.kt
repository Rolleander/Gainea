package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.SelectionUtils
import com.broll.gainea.server.core.utils.UnitControl

class C_KillSoldier : Card(34, "Scharfschütze", "Tötet eine beliebige feindliche Einheit (Außer Feldherr)") {
    init {
        drawChance = 0.6f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val unit = SelectionUtils.selectOtherPlayersUnit(game, owner, "Wählt eine Einheit aus die vernichtet werden soll"
        ) { !PlayerUtils.isCommander(it) }
        if (unit != null) {
            UnitControl.kill(game, unit)
        }
    }
}

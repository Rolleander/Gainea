package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.SelectionUtils
import com.broll.gainea.server.core.utils.UnitControl

class C_TakeOver : Card(17, "Treueschwur", "Übernehmt die Einheit eines anderen Spielers (Ausser Feldherr)" +
        " und teleportiert sie zu eurem Feldherr.") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = PlayerUtils.isCommanderAlive(owner) && targets.isNotEmpty()
    private val targets: List<Unit>
        get() = PlayerUtils.getOtherPlayers(game, owner).flatMap { it.units }.filter { !PlayerUtils.isCommander(it) }

    override fun play() {
        val unit = SelectionUtils.selectUnit(game, owner, "Welche Einheit soll übernommen werden?", targets)
        UnitControl.recruit(game, owner, listOf(unit), PlayerUtils.getCommander(owner)!!.location)
    }
}

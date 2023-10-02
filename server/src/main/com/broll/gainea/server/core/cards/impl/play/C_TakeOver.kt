package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.UnitControl.recruit
import com.broll.gainea.server.core.utils.getCommander
import com.broll.gainea.server.core.utils.getOtherPlayers
import com.broll.gainea.server.core.utils.isCommander
import com.broll.gainea.server.core.utils.isCommanderAlive
import com.broll.gainea.server.core.utils.selectUnit

class C_TakeOver : Card(17, "Treueschwur", "Übernehmt die Einheit eines anderen Spielers (Ausser Feldherr)" +
        " und teleportiert sie zu eurem Feldherr.") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = owner.isCommanderAlive() && targets.isNotEmpty()
    private val targets: List<Unit>
        get() = game.getOtherPlayers(owner).flatMap { it.units }.filter { !it.isCommander() }

    override fun play() {
        val unit = game.selectUnit(owner, "Welche Einheit soll übernommen werden?", targets)!!
        game.recruit(owner, listOf(unit), owner.getCommander()?.location)
    }
}

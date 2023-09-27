package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.broll.gainea.server.core.utils.SelectionUtilsimport com.broll.gainea.server.core.utils.UnitControlimport com.google.common.collect.Listsimport java.util.stream.Collectors
class C_TakeOver : Card(17, "Treueschwur", "Übernehmt die Einheit eines anderen Spielers (Ausser Feldherr)" +
        " und teleportiert sie zu eurem Feldherr.") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = PlayerUtils.isCommanderAlive(owner) && !targets.isEmpty()
    private val targets: List<Unit?>
        private get() = PlayerUtils.getOtherPlayers(game!!, owner).flatMap { it: Player? -> it.getUnits().stream() }.filter { it: Unit? -> !PlayerUtils.isCommander(it) }.collect(Collectors.toList())

    override fun play() {
        val unit = SelectionUtils.selectUnit(game!!, owner, "Welche Einheit soll übernommen werden?", targets)
        UnitControl.recruit(game!!, owner!!, Lists.newArrayList(unit), PlayerUtils.getCommander(owner).get().location)
    }
}

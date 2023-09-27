package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.PlayerUtilsimport java.util.stream.Collectors
class C_StealCard : Card(13, "Bekehrung", "Übernehmt eine Aktionskarte von einem beliebigen Spieler") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = !stealTargets.isEmpty()
    private val stealTargets: List<Player?>
        private get() = PlayerUtils.getOtherPlayers(game!!, owner).filter { it: Player? -> !it.getCardHandler().cards.isEmpty() }.collect(Collectors.toList())

    override fun play() {
        val player = selectHandler!!.selectPlayer(owner, stealTargets, "Welchen Spieler bekehren?")
        val cards = player.cardHandler.cards
        val card = cards!![selectHandler!!.selectObject("Wählt eine Karte", cards.stream().map { obj: Card? -> obj!!.nt() }.collect(Collectors.toList()))]
        player.cardHandler.discardCard(card)
        owner.cardHandler.receiveCard(card)
    }
}

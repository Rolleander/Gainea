package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.getOtherPlayers
import com.broll.gainea.server.core.utils.sendUpdate

class C_StealCard :
    Card(13, "Bekehrung", "Übernehmt eine Aktionskarte von einem beliebigen Spieler") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = stealTargets.isNotEmpty()
    private val stealTargets: List<Player>
        get() = game.getOtherPlayers(owner).filter { it.cardHandler.cards.isNotEmpty() }

    override fun play() {
        val player = selectHandler.selectPlayer(owner, stealTargets, "Welchen Spieler bekehren?")
        val cards = player.cardHandler.cards
        val card = cards[selectHandler.selectObject("Wählt eine Karte", cards.map { it.nt() })]
        player.cardHandler.discardCard(card)
        owner.cardHandler.receiveCard(card)
        //update card counts in all clients
        game.sendUpdate(game.nt())
    }
}

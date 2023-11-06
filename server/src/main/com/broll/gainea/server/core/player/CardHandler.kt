package com.broll.gainea.server.core.player

import com.broll.gainea.net.NT_Event_OtherPlayerReceivedCard
import com.broll.gainea.net.NT_Event_ReceivedCard
import com.broll.gainea.net.NT_Event_RemoveCard
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.playCard
import com.broll.gainea.server.core.utils.sendUpdate

class CardHandler(private val game: Game, private val player: Player) {
    val cards = mutableListOf<Card>()
    fun drawRandomCard() {
        game.cardStorage.drawRandomCard(player)
    }

    fun drawRandomPlayableCard() {
        receiveCard(game.cardStorage.getRandomPlayableCard())
    }

    fun receiveCard(card: Card) {
        if (player.isNeutral()) return
        card.init(game, player)
        if (card is DirectlyPlayedCard) {
            game.playCard(card)
            return
        }
        cards.add(card)
        val nt = NT_Event_ReceivedCard()
        nt.card = card.nt()
        nt.sound = "chime.ogg"
        val nt2 = NT_Event_OtherPlayerReceivedCard()
        nt2.player = player.serverPlayer.id
        game.sendUpdate(player, nt, nt2)
    }

    fun discardCard(card: Card) {
        if (cards.remove(card)) {
            val nt = NT_Event_RemoveCard()
            nt.card = card.nt()
            player.serverPlayer.sendTCP(nt)
        }
    }

    val cardCount: Int
        get() = cards.size

    fun ntCards() = cards.map { it.nt() }.toTypedArray()

}

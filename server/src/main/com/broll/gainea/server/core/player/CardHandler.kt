package com.broll.gainea.server.core.player

import com.broll.gainea.net.proto.NT_Event_OtherPlayerReceivedCard
import com.broll.gainea.net.proto.NT_Event_ReceivedCard
import com.broll.gainea.net.proto.NT_Event_RemoveCard
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
        val nt = NT_Event_ReceivedCard.newBuilder().setCard(card.nt()).setSound("chime.ogg").build()
        val nt2 = NT_Event_OtherPlayerReceivedCard.newBuilder()
            .setPlayer(player.serverPlayer.id).build()
        game.sendUpdate(player, nt, nt2)
    }

    fun discardCard(card: Card) {
        if (cards.remove(card)) {
            player.serverPlayer.send(NT_Event_RemoveCard.newBuilder().setCard(card.nt()).build())
        }
    }

    val cardCount: Int
        get() = cards.size

    fun ntCards() = cards.map { it.nt() }

}

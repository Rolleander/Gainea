package com.broll.gainea.server.core.events

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.cards.playCard

abstract class EventCard(picture: Int, title: String, text: String) :
    DirectlyPlayedCard(picture, title, text)

fun Game.runEvent(event: EventCard) {
    event.init(this, neutralPlayer)
    playCard(event)
}

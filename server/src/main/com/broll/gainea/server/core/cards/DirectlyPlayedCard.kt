package com.broll.gainea.server.core.cards

import com.broll.gainea.core.cards.EffectType
import com.broll.gainea.net.proto.NT_Card

abstract class DirectlyPlayedCard(
    picture: Int,
    effectType: EffectType,
    title: String,
    text: String
) : Card(picture, effectType, title, text) {
    override val isPlayable: Boolean
        get() = true

    override fun nt(): NT_Card {
        return super.nt().toBuilder().setPlayable(false).build()
    }
}

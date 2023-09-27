package com.broll.gainea.server.core.cards

import com.broll.gainea.net.NT_Card

abstract class DirectlyPlayedCard(picture: Int, title: String, text: String) : Card(picture, title, text) {
    override val isPlayable: Boolean
        get() = true

    override fun nt(): NT_Card? {
        val nt = super.nt()
        nt!!.playable = false
        return nt
    }
}

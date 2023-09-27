package com.broll.gainea.server.core.cards.impl.play

import com.google.common.collect.Lists

com.broll.gainea.server.core.cards.Card
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListener

class C_Knowledge : Card(29, "Bürokratie", "Wählt einer der folgenden Effekte: \n\n- Platziert einen Soldaten \n- Erhaltet " + STARS + " Sterne \n- Erhaltet eine zufällige Aktionskarte") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val option = selectHandler!!.selection("Wählt einen Effekt",
                Lists.newArrayList("Platziert einen Soldaten", "Erhaltet " + STARS + " Sterne", "Erhaltet eine zufällige Aktionskarte"))
        when (option) {
            0 -> placeUnitHandler!!.placeSoldier(owner)
            1 -> owner.goalHandler.addStars(STARS)
            2 -> owner.cardHandler.drawRandomCard()
        }
    }

    companion object {
        private const val STARS = 5
    }
}

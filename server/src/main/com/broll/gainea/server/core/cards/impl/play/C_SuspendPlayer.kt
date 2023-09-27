package com.broll.gainea.server.core.cards.impl.play

com.broll.gainea.server.core.cards.Card
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListener

class C_SuspendPlayer : Card(12, "In den Kerker", "Ein Spieler deiner Wahl muss eine Runde aussetzen") {
    init {
        drawChance = 0.4f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        selectHandler!!.selectOtherPlayer(owner, "Spieler der Aussetzen muss:")!!.skipRounds(1)
    }
}

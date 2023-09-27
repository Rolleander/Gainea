package com.broll.gainea.server.core.cards.impl.play

com.broll.gainea.server.core.cards.Card
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListener

class C_Treasury : Card(3, "Reichtum", "Erhaltet ein weiteres Ziel") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        game.goalStorage.assignNewRandomGoal(owner)
    }
}

package com.broll.gainea.test

import com.broll.gainea.server.core.Game
import com.broll.gainea.core.fractions.FractionType
import com.broll.gainea.core.map.ExpansionSetting
import com.broll.gainea.core.map.ExpansionSetting.FULL
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.mpnll.server.MpnllServer
import com.broll.mpnll.server.user.User
import com.google.protobuf.Any
import com.google.protobuf.Message

private class TestUser(id: Int, name: String) : User(id, "test:$id", null) {
    init { setName(name) }
    override fun send(message: Message) = Unit
    override fun send(data: ByteArray) = Unit
}

fun testGame(expansionSetting: ExpansionSetting = FULL, playersCount: Int = 0): Game {
    val lobby = MpnllServer().lobbyHandler.openLobby {
        it.name = "test"
        it.data = LobbyData()
        it.isAutoClose = false
        it.setLobbySettingsBuilder { lobby -> Any.pack((lobby.data as LobbyData).nt()) }
        it.setUserSettingsBuilder { user -> Any.pack((user.data as PlayerData).nt()) }
    }
    (1..playersCount).forEach {
        val player = TestUser(it, "Tester#$it")
        player.name = "Tester#$it"
        player.data = PlayerData(FractionType.entries.random())
        lobby.addUser(player)
    }
    (lobby.data as LobbyData).expansionSetting = expansionSetting
    return Game(lobby)
}

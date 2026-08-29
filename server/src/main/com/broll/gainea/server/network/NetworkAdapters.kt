package com.broll.gainea.server.network

import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.mpnll.server.lobby.Lobby
import com.broll.mpnll.server.user.User

val Lobby.gameData: LobbyData
    get() = data as LobbyData

val User.playerData: PlayerData
    get() = data as PlayerData

val Lobby.players: Collection<User>
    get() = allUsers

val Lobby.playersData: List<PlayerData>
    get() = activeUsers.map { it.playerData }

fun Lobby.publishUpdate() = updatePublisher.sendUpdate()

fun Lobby.systemMessage(message: String, recipient: User? = null) {
    val chat = com.broll.mpnll.nt.NT_ChatMessage.newBuilder().setMessage(message).build()
    if (recipient == null) sendToAll(chat) else recipient.send(chat)
}

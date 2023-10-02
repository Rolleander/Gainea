package com.broll.gainea.server

import com.broll.gainea.server.init.IGameStartListener
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.ILobbyServerListener
import com.broll.networklib.server.LobbyGameServer
import com.broll.networklib.server.impl.Player
import com.broll.networklib.server.impl.ServerLobby
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

class ServerStatistic(private val server: LobbyGameServer<LobbyData, PlayerData>) :
        ILobbyServerListener<LobbyData, PlayerData> {
    private val file = File(STATISTIC_FILE)

    init {
        writeStatistic()
    }

    private fun writeStatistic() {
        CompletableFuture.runAsync {
            val json = JSONObject()
            val iterator: Iterator<ServerLobby<LobbyData, PlayerData>> = server.lobbyHandler.lobbies.iterator()
            var players = 0
            var games = 0
            while (iterator.hasNext()) {
                val lobby = iterator.next()
                players += lobby.realPlayers.size
                if (lobby.data.game != null) {
                    games++
                }
            }
            json.put("players", players)
            json.put("games", games)
            try {
                FileUtils.write(file, json.toString(), StandardCharsets.UTF_8)
            } catch (e: IOException) {
                Log.error("Failed to write " + STATISTIC_FILE, e)
            }
        }
    }

    override fun lobbyOpened(lobby: ServerLobby<LobbyData, PlayerData>) {
        lobby.data.gameStartListener =  object : IGameStartListener {
            override fun gameStarted() {
                writeStatistic()
            }
        }
    }

    override fun playerJoined(lobby: ServerLobby<LobbyData, PlayerData>, player: Player<PlayerData>) {
        writeStatistic()
    }

    override fun playerLeft(lobby: ServerLobby<LobbyData, PlayerData>, player: Player<PlayerData>) {
        writeStatistic()
    }

    override fun playerDisconnected(lobby: ServerLobby<LobbyData, PlayerData>, player: Player<PlayerData>) {
        writeStatistic()
    }

    override fun playerReconnected(lobby: ServerLobby<LobbyData, PlayerData>, player: Player<PlayerData>) {
        writeStatistic()
    }

    override fun lobbyClosed(lobby: ServerLobby<LobbyData, PlayerData>) {
        writeStatistic()
    }

    companion object {
        private val Log = LoggerFactory.getLogger(ServerStatistic::class.java)
        private const val STATISTIC_FILE = "stats.json"
    }
}

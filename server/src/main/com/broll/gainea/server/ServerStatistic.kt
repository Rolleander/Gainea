package com.broll.gainea.server

import com.broll.gainea.server.init.IGameStartListener
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.gainea.server.network.gameData
import com.broll.mpnll.server.MpnllServer
import com.broll.mpnll.server.lobby.Lobby
import com.broll.mpnll.server.lobby.LobbyStateListener
import com.broll.mpnll.server.lobby.LobbyUsersListener
import com.broll.mpnll.server.user.User
import org.apache.commons.io.FileUtils
import org.json.JSONObject
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

class ServerStatistic(private val server: MpnllServer) : LobbyStateListener, LobbyUsersListener {
    private val file = File(STATISTIC_FILE)

    init {
        server.lobbyHandler.addStateListener(this)
        server.lobbyHandler.addUsersListener(this)
        writeStatistic()
    }

    private fun writeStatistic() {
        CompletableFuture.runAsync {
            val json = JSONObject()
            val iterator: Iterator<Lobby> = server.lobbyHandler.listAll().iterator()
            var players = 0
            var games = 0
            while (iterator.hasNext()) {
                val lobby = iterator.next()
                players += lobby.activeUsers.count { !it.isBot }
                if (lobby.gameData.game != null) {
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

    override fun lobbyOpened(lobby: Lobby) {
        lobby.gameData.gameStartListener =  object : IGameStartListener {
            override fun gameStarted() {
                writeStatistic()
            }
        }
    }

    override fun userJoined(lobby: Lobby, player: User) {
        writeStatistic()
    }

    override fun userLeft(lobby: Lobby, player: User) {
        writeStatistic()
    }

    override fun userDisconnected(lobby: Lobby, player: User) {
        writeStatistic()
    }

    override fun userReconnected(lobby: Lobby, player: User) {
        writeStatistic()
    }

    override fun lobbyClosed(lobby: Lobby) {
        writeStatistic()
    }

    override fun lobbyLocked(lobby: Lobby) = Unit
    override fun lobbyUnlocked(lobby: Lobby) = Unit

    companion object {
        private val Log = LoggerFactory.getLogger(ServerStatistic::class.java)
        private const val STATISTIC_FILE = "stats.json"
    }
}

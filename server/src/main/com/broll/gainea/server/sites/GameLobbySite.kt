package com.broll.gainea.server.sites

import com.broll.gainea.misc.EnumUtils
import com.broll.gainea.net.NT_AddBot
import com.broll.gainea.net.NT_PlayerChangeFraction
import com.broll.gainea.net.NT_PlayerReady
import com.broll.gainea.net.NT_UpdateLobbySettings
import com.broll.gainea.server.core.bot.BotPlayerSite
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.init.ExpansionSetting
import com.broll.gainea.server.init.GoalTypes
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.PackageReceiver
import com.broll.networklib.server.ConnectionRestriction
import com.broll.networklib.server.LobbyGameServer
import com.broll.networklib.server.LobbyServerSite
import com.broll.networklib.server.RestrictionType
import com.broll.networklib.server.impl.LobbyHandler
import com.broll.networklib.server.impl.Player
import org.slf4j.LoggerFactory

class GameLobbySite : LobbyServerSite<LobbyData, PlayerData>() {
    override fun init(server: LobbyGameServer<LobbyData, PlayerData>, lobbyHandler: LobbyHandler<LobbyData, PlayerData>) {
        super.init(server, lobbyHandler)
        this.lobbyHandler.setLobbyCreationRequestHandler { requester: Player<PlayerData>, lobbyName: String, settings: Any? ->
            val lobby = lobbyHandler.openLobby(lobbyName, LobbyData())
            lobby.addListener(LobbyListener())
            lobby.playerLimit = 9
            lobby
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_UNLOCKED)
    fun changeFraction(change: NT_PlayerChangeFraction) {
        val newFraction = change.fraction
        if (EnumUtils.inBounds(newFraction, FractionType::class.java)) {
            val fraction = FractionType.entries[newFraction]
            //check if its free
            if (!lobby.playersData.map { it.fraction }.any { it == fraction }) {
                //update player fraction
                player.data.fraction = fraction
                lobby.sendLobbyUpdate()
            }
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_UNLOCKED)
    fun changeSettings(nt: NT_UpdateLobbySettings) {
        if (player !== lobby.owner) {
            Log.warn("Ignore change settings from $player because he is not owner of the lobby")
            return
        }
        val data = lobby.data
        val value = nt.value
        when (nt.setting) {
            NT_UpdateLobbySettings.SETTING_EXPANSIONS -> if (EnumUtils.inBounds(value, ExpansionSetting::class.java)) {
                data.expansionSetting = ExpansionSetting.entries[value]
            }

            NT_UpdateLobbySettings.SETTING_GOAL_TYPES -> if (EnumUtils.inBounds(value, GoalTypes::class.java)) {
                data.goalTypes = GoalTypes.entries[value]
            }

            NT_UpdateLobbySettings.SETTING_POINT_LIMIT -> if (value >= 0) {
                data.pointLimit = value
            }

            NT_UpdateLobbySettings.SETTING_START_GOALS -> if (value > 0) {
                data.startGoals = value
            }

            NT_UpdateLobbySettings.SETTING_START_LOCATIONS -> if (value > 0) {
                data.startLocations = value
            }

            NT_UpdateLobbySettings.SETTING_MONSTERS -> if (value >= 0) {
                data.monsterCount = value
            }

            NT_UpdateLobbySettings.SETTING_ROUND_LIMIT -> if (value >= 0) {
                data.roundLimit = value
            }
        }
        lobby.sendLobbyUpdate()
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_UNLOCKED)
    fun addBot(nt: NT_AddBot) {
        val lobby = lobby
        if (!lobby.isFull) {
            val data = PlayerData(FractionType.entries.random())
            data.ready = true
            lobbyHandler.createBot(lobby, "bot_" + lobby.players.size, data).ifPresent { it.register(BotPlayerSite()) }
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_UNLOCKED)
    fun ready(ready: NT_PlayerReady) {
        player.data.ready = ready.ready
        lobby.sendLobbyUpdate()
        //check for all ready, then lock lobby and start game
        val lobby = lobby
        synchronized(lobby) {
            if (lobby.playersData.all { it.ready }) {
                lobby.lock()
                accessSite(GameStartSite::class.java).startGame()
            }
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(GameLobbySite::class.java)
    }
}

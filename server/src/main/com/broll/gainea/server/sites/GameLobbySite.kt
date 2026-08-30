package com.broll.gainea.server.sites

import com.broll.gainea.misc.EnumUtils
import com.broll.gainea.net.proto.NT_AddBot
import com.broll.gainea.net.proto.NT_PlayerChangeFraction
import com.broll.gainea.net.proto.NT_PlayerReady
import com.broll.gainea.net.proto.NT_UpdateLobbySettings
import com.broll.gainea.server.core.bot.BotPlayerSite
import com.broll.gainea.core.fractions.FractionType
import com.broll.gainea.core.map.ExpansionSetting
import com.broll.gainea.core.goals.GoalTypes
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.gainea.server.network.gameData
import com.broll.gainea.server.network.playerData
import com.broll.gainea.server.network.players
import com.broll.gainea.server.network.playersData
import com.broll.gainea.server.network.publishUpdate
import com.broll.mpnll.server.MpnllServer
import com.broll.mpnll.server.lobby.LobbyHandler
import com.broll.mpnll.server.site.NetworkSite
import com.broll.mpnll.server.site.PackageReceiver
import com.broll.mpnll.server.utils.ConnectionRestriction
import com.broll.mpnll.server.utils.RestrictionType
import com.broll.mpnll.server.user.BotUser
import com.google.protobuf.Any
import org.slf4j.LoggerFactory

class GameLobbySite : NetworkSite() {
    override fun init(server: MpnllServer, lobbyHandler: LobbyHandler) {
        super.init(server, lobbyHandler)
        lobbyHandler.acceptLobbyCreation { _, lobby, _: Any ->
            lobby.data = LobbyData()
            lobby.setLobbySettingsBuilder { value -> Any.pack(value.gameData.nt()) }
            lobby.setUserSettingsBuilder { value -> Any.pack(value.playerData.nt()) }
            val listener = LobbyListener()
            lobby.addUsersListener(listener)
            lobby.chatHandler = CustomLobbySite()
            lobby.playerLimit = 9
            true
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
                user.playerData.fraction = fraction
                lobby.publishUpdate()
            }
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_UNLOCKED)
    fun changeSettings(nt: NT_UpdateLobbySettings) {
        if (user !== lobby.owner) {
            Log.warn("Ignore change settings from $user because he is not owner of the lobby")
            return
        }
        val data = lobby.gameData
        val value = nt.value
        when (nt.setting) {
            SETTING_EXPANSIONS -> if (EnumUtils.inBounds(value, ExpansionSetting::class.java)) {
                data.expansionSetting = ExpansionSetting.entries[value]
            }

            SETTING_GOAL_TYPES -> if (EnumUtils.inBounds(value, GoalTypes::class.java)) {
                data.goalTypes = GoalTypes.entries[value]
            }

            SETTING_POINT_LIMIT -> if (value >= 0) {
                data.pointLimit = value
            }

            SETTING_START_GOALS -> if (value > 0) {
                data.startGoals = value
            }

            SETTING_START_LOCATIONS -> if (value > 0) {
                data.startLocations = value
            }

            SETTING_MONSTERS -> if (value >= 0) {
                data.monsterCount = value
            }

            SETTING_ROUND_LIMIT -> if (value >= 0) {
                data.roundLimit = value
            }
        }
        lobby.publishUpdate()
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_UNLOCKED)
    fun addBot(nt: NT_AddBot) {
        val lobby = lobby
        if (!lobby.isFull) {
            val data = PlayerData(FractionType.entries.random())
            data.ready = true
            val bot = lobbyHandler.createBot(lobby, "bot_" + lobby.players.size, data)
            if (bot is BotUser) {
                val controller = BotPlayerSite(lobby, bot)
                bot.setMessageReceiver(controller::receive)
            }
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_UNLOCKED)
    fun ready(ready: NT_PlayerReady) {
        user.playerData.ready = ready.ready
        lobby.publishUpdate()
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
        private const val SETTING_EXPANSIONS = 0
        private const val SETTING_START_LOCATIONS = 1
        private const val SETTING_START_GOALS = 2
        private const val SETTING_POINT_LIMIT = 3
        private const val SETTING_GOAL_TYPES = 4
        private const val SETTING_MONSTERS = 5
        private const val SETTING_ROUND_LIMIT = 6
    }
}

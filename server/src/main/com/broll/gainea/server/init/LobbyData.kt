package com.broll.gainea.server.init

import com.broll.gainea.net.NT_LobbySettings
import com.broll.gainea.server.core.Game
import com.broll.networklib.server.impl.ILobbyData

class LobbyData : ILobbyData {
    var expansionSetting = ExpansionSetting.BASIC_GAME
    var goalTypes = GoalTypes.ALL
    var startLocations = START_LOCATIONS_DEFAULT
    var startGoals = START_GOALS_DEFAULT
    var pointLimit = POINT_LIMIT_DEFAULT
    var monsterCount = MONSTERS_PER_MAP
    var roundLimit = ROUND_LIMIT_DEFAULT
    var game: Game? = null
    var gameStartListener: IGameStartListener? = null
    var gameRoundsStarted = false
    override fun nt(): NT_LobbySettings {
        val settings = NT_LobbySettings()
        settings.expansionSetting = expansionSetting.ordinal
        settings.startGoals = startGoals
        settings.startLocations = startLocations
        settings.pointLimit = pointLimit
        settings.goalTypes = goalTypes.ordinal
        settings.monsters = monsterCount
        settings.roundLimit = roundLimit
        return settings
    }

    companion object {
        private const val START_LOCATIONS_DEFAULT = 3
        private const val START_GOALS_DEFAULT = 3
        private const val POINT_LIMIT_DEFAULT = 0
        private const val ROUND_LIMIT_DEFAULT = 20
        private const val MONSTERS_PER_MAP = 10
    }
}

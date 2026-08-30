package com.broll.gainea.server.init

import com.broll.gainea.core.map.ExpansionSetting
import com.broll.gainea.core.goals.GoalTypes
import com.broll.gainea.net.proto.NT_LobbySettings
import com.broll.gainea.server.core.Game
import com.broll.mpnll.server.user.User

class LobbyData {
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
    var playerLoaded: ((User) -> Unit)? = null
    fun nt(): NT_LobbySettings = NT_LobbySettings.newBuilder()
        .setExpansionSetting(expansionSetting.ordinal)
        .setStartGoals(startGoals)
        .setStartLocations(startLocations)
        .setPointLimit(pointLimit)
        .setGoalTypes(goalTypes.ordinal)
        .setMonsters(monsterCount)
        .setRoundLimit(roundLimit)
        .build()

    companion object {
        private const val START_LOCATIONS_DEFAULT = 3
        private const val START_GOALS_DEFAULT = 3
        private const val POINT_LIMIT_DEFAULT = 0
        private const val ROUND_LIMIT_DEFAULT = 20
        private const val MONSTERS_PER_MAP = 10
    }
}

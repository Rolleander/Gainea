package com.broll.gainea.server.sites

import com.broll.gainea.net.NT_LoadedGame
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.ReactionResultHandler
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.impl.play.C_PickCard
import com.broll.gainea.server.core.cards.impl.play.C_ReplaceGoal
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.UnitControl.spawnMonsters
import com.broll.gainea.server.core.utils.pickRandomEmpty
import com.broll.networklib.PackageReceiver
import com.broll.networklib.server.Autoshared
import com.broll.networklib.server.ConnectionRestriction
import com.broll.networklib.server.RestrictionType
import com.broll.networklib.server.ShareLevel
import com.google.common.collect.Lists
import org.slf4j.LoggerFactory

class GameStartSite : GameSite() {
    private inner class GameStartData {
        var loading = true
        val playerData = HashMap<Player, PlayerStartData>()
    }

    private inner class PlayerStartData {
        var loaded = false
    }

    @Autoshared(ShareLevel.LOBBY)
    private lateinit var gameStart: GameStartData

    fun startGame() {
        val lobby = lobby
        lobby.chat(null, "Starte Spiel...")
        val game = Game(lobby)
        lobby.data.gameStartListener?.gameStarted()
        game.initHandlers(ReactionResultHandler(game, lobby))
        gameStart.loading = true
        lobby.players.forEach { p ->
            val player = p.data.gamePlayer
            gameStart.playerData[player] = PlayerStartData()
            drawStartingCards(game, player)
            p.sendTCP(game.start(player))
        }
        Log.info("Started game in lobby " + lobby.name)
    }

    private fun drawStartingCards(game: Game, player: Player) {
        STARTING_CARDS.forEach { cardClass ->
            val card = game.cardStorage.getCard(cardClass)
            card.init(game, player)
            player.cardHandler.cards.add(card)
        }
    }

    private fun gameLoaded() {
        gameStart.loading = false
        game.processingCore.execute({
            //spawn monsters
            val totalMonsters = lobby.data.monsterCount * game.map.activeExpansionTypes.size
            Log.info("Spawn Monsters: $totalMonsters")
            game.spawnMonsters(totalMonsters)
            ProcessingUtils.pause(DELAY)
            //give random goals and start locations to everyone
            assignGoals()
            //players start placing units
            placeUnits()
            //all start units placed, start first turn
            ProcessingUtils.MAX_PAUSE *= 3
            lobby.data.gameRoundsStarted = true
            Log.info("Start first turn")
            nextTurn()
        }, DELAY)
    }

    private fun assignGoals() {
        Log.info("Assign Goals")
        val game = game
        val startGoalsCount = lobby.data.startGoals
        for (i in 0 until startGoalsCount) {
            game.allPlayers.forEach { player ->
                Log.debug("Add goal to $player")
                game.goalStorage.assignNewRandomGoal(player)
                ProcessingUtils.pause(DELAY)
            }
        }
    }

    private fun placeUnits() {
        Log.info("Draw start locations")
        val game = game
        val startLocationsCount = lobby.data.startLocations
        val playerCount = game.allPlayers.size
        val startLocations =
            game.map.pickRandomEmpty(playerCount * startLocationsCount).toMutableList()
        for (player in game.allPlayers) {
            if (!player.active) {
                continue
            }
            val playerStartLocations = startLocations.take(startLocationsCount)
            startLocations.removeAll(playerStartLocations)
            placePlayerUnits(player, playerStartLocations)
        }
    }

    private fun placePlayerUnits(player: Player, locations: List<Location>) {
        Log.info("placePlayerUnits $player")
        val game = game
        val unitToPlace = player.fraction.createCommander()
        val actionHandlers = game.reactionHandler.actionHandlers
        val placeUnitAction = actionHandlers.getHandler(PlaceUnitAction::class.java)
        val result = placeUnitAction.placeUnit(player, unitToPlace, locations)
        val pickedLocation = result.right
        locations.filter { it != pickedLocation }.forEach {
            game.spawn(player.fraction.createSoldier(), it)
        }
    }

    @PackageReceiver //   @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun playerLoaded(loadedGame: NT_LoadedGame) {
        if (gameStart.loading) {
            Log.info("$gamePlayer loaded game!")
            gameStart.playerData[gamePlayer]!!.loaded = true
            if (allPlayersLoaded()) {
                Log.info("All players loaded game, start with init!")
                gameLoaded()
            }
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun playerReconnectFinished(loadedGame: NT_LoadedGame) {
        if (!gameStart.loading) {
            game.reactionHandler.playerReconnected(gamePlayer)
        }
    }

    private fun allPlayersLoaded() = gameStart.playerData.values.all { it.loaded }

    companion object {
        private val Log = LoggerFactory.getLogger(GameStartSite::class.java)
        private val STARTING_CARDS: List<Class<out Card?>> =
            Lists.newArrayList(C_ReplaceGoal::class.java, C_PickCard::class.java)
        private const val DELAY = 1000
    }
}

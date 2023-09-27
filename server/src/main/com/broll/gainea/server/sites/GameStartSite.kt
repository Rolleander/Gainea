package com.broll.gainea.server.sites

import com.broll.gainea.net.NT_LoadedGame
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.ReactionResultHandler
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.impl.play.C_PickCard
import com.broll.gainea.server.core.cards.impl.play.C_ReplaceGoal
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.UnitControl
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.PackageReceiver
import com.broll.networklib.server.Autoshared
import com.broll.networklib.server.ShareLevel
import com.broll.networklib.server.impl.LobbyPlayer
import com.google.common.collect.Lists
import org.slf4j.LoggerFactory
import java.util.function.Consumer
import java.util.stream.Collectors

class GameStartSite : GameSite() {
    private inner class GameStartData {
        var loading = true
        var startUnitsPlaced = 0
        var playerData: MutableMap<Player?, PlayerStartData>? = null
    }

    private inner class PlayerStartData {
        var loaded = false
        var startLocations: MutableList<Location?>? = null
    }

    @Autoshared(ShareLevel.LOBBY)
    private val gameStart: GameStartData? = null
    fun startGame() {
        val lobby = lobby
        lobby.chat(null, "Starte Spiel...")
        val game = GameContainer(lobby)
        lobby.data.gameStartListener.gameStarted()
        game.initHandlers(ReactionResultHandler(game, lobby))
        gameStart!!.loading = true
        gameStart.startUnitsPlaced = 0
        gameStart.playerData = HashMap()
        lobby.players.forEach(Consumer { p: LobbyPlayer<PlayerData?> ->
            val player = p.data.getGamePlayer()
            gameStart.playerData[player] = PlayerStartData()
            drawStartingCards(game, player)
            p.sendTCP(game.start(player))
        })
        Log.info("Started game in lobby " + lobby.name)
    }

    private fun drawStartingCards(game: GameContainer, player: Player?) {
        STARTING_CARDS.forEach(Consumer { cardClass: Class<out Card?>? ->
            val card = game.cardStorage.getCard(cardClass)
            card!!.init(game, player, game.newObjectId())
            player.getCardHandler().cards.add(card)
        }
        )
    }

    private fun gameLoaded() {
        gameStart!!.loading = false
        game.processingCore.execute({

            //spawn monsters
            val totalMonsters = lobby.data.monsterCount * game.map.activeExpansionTypes.size
            Log.info("Spawn Monsters: $totalMonsters")
            UnitControl.spawnMonsters(game, totalMonsters)
            ProcessingUtils.pause(DELAY)
            //give random goals and start locations to everyone
            drawStartLocations()
            assignGoals()
            //players start placing units
            placeUnit()
        }, DELAY)
    }

    private fun assignGoals() {
        Log.info("Assign Goals")
        val game = game
        val startGoalsCount = lobby.data.startGoals
        for (i in 0 until startGoalsCount) {
            game.allPlayers.forEach(Consumer { player: Player? ->
                Log.debug("Add goal to $player")
                game.goalStorage.assignNewRandomGoal(player)
                ProcessingUtils.pause(DELAY)
            })
        }
    }

    private fun drawStartLocations() {
        Log.info("Draw start locations")
        val game = game
        val startLocationsCount = lobby.data.startLocations
        val playerCount = game.allPlayers.size
        val startLocations = LocationUtils.pickRandomEmpty(game.map, playerCount * startLocationsCount)
        for (player in game.allPlayers) {
            val playerStartLocations = startLocations!!.stream().limit(startLocationsCount.toLong()).collect(Collectors.toList())
            startLocations.removeAll(playerStartLocations)
            gameStart!!.playerData!![player]!!.startLocations = playerStartLocations
        }
    }

    private fun placeUnit() {
        Log.info("placeUnit")
        val game = game
        val playerCount = playersCount
        val placingRound = gameStart!!.startUnitsPlaced / playerCount
        val player = placingPlayer
        val locations: List<Location?>? = gameStart.playerData!![player]!!.startLocations
        val unitToPlace: Unit?
        unitToPlace = if (placingRound == 0) {
            player.getFraction().createCommander()
        } else {
            player.getFraction().createSoldier()
        }
        val text = "Setze " + unitToPlace.getName() + " auf einen Startpunkt"
        val actionHandlers = game.reactionHandler.actionHandlers
        val placeUnitAction = actionHandlers!!.getHandler(PlaceUnitAction::class.java)
        val result = placeUnitAction!!.placeUnit(player, unitToPlace, locations, text)
        placedUnit(result!!.right)
    }

    private fun placedUnit(location: Location?) {
        Log.info("player placedUnit")
        val game = game
        val startLocationsCount = lobby.data.startLocations
        val player = placingPlayer
        //remove selected location from start locations
        gameStart!!.playerData!![player]!!.startLocations!!.remove(location)
        gameStart.startUnitsPlaced++
        if (gameStart.startUnitsPlaced < game.allPlayers.size * startLocationsCount) {
            //next player places unit
            placeUnit()
        } else {
            //all start units placed, start first turn
            ProcessingUtils.MAX_PAUSE *= 3
            nextTurn()
            lobby.data.isGameRoundsStarted = true
        }
    }

    private val placingPlayer: Player?
        private get() {
            val playerNr = gameStart!!.startUnitsPlaced % playersCount
            return game.allPlayers[playerNr]
        }

    @PackageReceiver //   @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun playerLoaded(loadedGame: NT_LoadedGame?) {
        if (gameStart!!.loading) {
            Log.info("$gamePlayer loaded game!")
            gameStart.playerData!![gamePlayer]!!.loaded = true
            if (allPlayersLoaded()) {
                Log.info("All players loaded game, start with init!")
                gameLoaded()
            }
        }
    }

    private fun allPlayersLoaded(): Boolean {
        return gameStart!!.playerData!!.values.stream().map { it: PlayerStartData -> it.loaded }.reduce(true) { a: Boolean, b: Boolean -> java.lang.Boolean.logicalAnd(a, b) }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(GameStartSite::class.java)
        private val STARTING_CARDS: List<Class<out Card?>> = Lists.newArrayList(C_ReplaceGoal::class.java, C_PickCard::class.java)
        private const val DELAY = 1000
    }
}

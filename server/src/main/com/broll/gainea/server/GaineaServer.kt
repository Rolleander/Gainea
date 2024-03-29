package com.broll.gainea.server

import com.broll.gainea.NetworkSetup
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.events.RandomEventContainer
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.endTurn
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.gainea.server.init.ServerSetup
import com.broll.gainea.server.sites.CustomLobbySite
import com.broll.networklib.server.LobbyGameServer
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class GaineaServer(version: String) {
    val server: LobbyGameServer<LobbyData, PlayerData>

    init {
        com.esotericsoftware.minlog.Log.INFO()
        Log.info("Start Gainea Server $version")
        server =
            LobbyGameServer("GaineaServer", CustomLobbySite()) { NetworkSetup.registerNetwork(it) }
        server.setVersion(version)
        ServerSetup.setup(server)
        server.open()
        server.addListener(ServerStatistic(server))
    }

    fun appendCLI() {
        LobbyServerCLI.open(server, nextTurn(), triggerEvent(), gameInfo(), giveCard())
    }

    private fun giveCard(): CliCommand {
        return LobbyServerCLI.cmd(
            "givecard",
            "gives current player a specific card"
        ) { options: List<String> ->
            if (options.size < 2) {
                System.err.println("givecard needs to be followed by a lobby id and card picture id")
                return@cmd
            }
            val id = options[0].toInt()
            val picId = options[1].toInt()
            getGame(id)?.let { game ->
                game.cardStorage.allCards.find { card -> card.picture == picId }?.let { card ->
                    game.currentPlayer.cardHandler.receiveCard(card)
                }
            }
        }
    }

    private fun triggerEvent(): CliCommand {
        return LobbyServerCLI.cmd("event", "triggers random event") { options: List<String> ->
            if (options.size < 1) {
                System.err.println("event must be followed by a lobby id")
                return@cmd
            }
            val id = options[0].toInt()
            getGame(id)?.let { game ->
                RandomEventContainer().run(game)
            }
        }
    }

    private fun nextTurn(): CliCommand {
        return LobbyServerCLI.cmd("nextturn", "Ends the current turn") { options: List<String> ->
            if (options.isEmpty()) {
                System.err.println("nextturn needs to be followed by a lobby id")
                return@cmd
            }
            val id = options[0].toInt()
            getGame(id)?.let { game ->
                game.battleHandler.reset()
                game.endTurn()
                print("Ended turn")
            }
        }
    }

    private fun gameInfo(): CliCommand {
        return LobbyServerCLI.cmd("game", "Info about a running game") { options: List<String> ->
            if (options.isEmpty()) {
                System.err.println("game needs to be followed by a lobby id")
                return@cmd
            }
            val id = options[0].toInt()
            getGame(id)?.let { game ->
                val settings = game.gameSettings
                print(
                    "> Game Settings: Map:" + settings.expansionSetting.getName() + " Goals:" + settings.goalTypes.goalName + " PointLimit:" + settings.pointLimit + " MonsterCount:" + settings.monsterCount
                            + " StartGoals:" + settings.startGoals + " StartLocations:" + settings.startLocations
                )
                print("> Round:" + game.rounds + " Turn:" + (game.currentTurn + 1) + "/" + game.allPlayers.size + " Player:" + if (game.currentTurn >= 0) game.currentPlayer.serverPlayer.name else "None")
                print("> State: ProcessingCore.Busy=" + game.processingCore.isBusy + " BattleHandler.Active=" + game.battleHandler.isBattleActive)
                print("> Game Objects [" + game.objects.size + "]:")
                game.objects.forEach(Consumer { `object`: MapObject? -> print(">> " + `object`.toString()) })
                game.allPlayers.forEach(Consumer { player: Player ->
                    print(
                        "> Player [" + player.toString() + " Points:" + player.goalHandler.score
                                + " Stars:" + player.goalHandler.stars + " Online=" + player.serverPlayer.isOnline + "]:"
                    )
                    print(
                        ">> Goals (" + player.goalHandler.goals.size + "):" + player.goalHandler.goals.joinToString(
                            ","
                        )
                    )
                    print(
                        ">> Cards (" + player.cardHandler.cards.size + "): " + player.cardHandler.cards.joinToString(
                            ","
                        )
                    )
                    print(">> Controlled Locations: [" + player.controlledLocations.joinToString(",") + "]")
                    print(">> Objects [" + player.units.size + "]:")
                    player.units.forEach { print(">>> $it") }
                })
            }
        }
    }

    private fun print(text: String) {
        println(text)
    }

    private fun getGame(lobbyId: Int): Game? {
        val lobby = server.lobbyHandler.getLobby(lobbyId)
        if (lobby == null) {
            System.err.println("Lobby #$lobbyId not found!")
        } else {
            val game = lobby.data.game
            if (game == null) {
                System.err.println("Lobby #$lobbyId has no active game!")
            } else {
                return game
            }
        }
        return null
    }

    companion object {
        private val Log = LoggerFactory.getLogger(GaineaServer::class.java)
    }
}

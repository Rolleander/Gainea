package com.broll.gainea.server.sites

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.events.RandomEventContainer
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.endTurn
import com.broll.gainea.server.init.LobbyData
import com.broll.mpnll.server.MpnllServer
import com.broll.mpnll.server.cli.ServerCLI

class CustomCliCommands(val server: MpnllServer) {

    fun register() {
        ServerCLI.register(server, giveCard(), triggerEvent(), nextTurn(), gameInfo())
    }

    private fun giveCard() = ServerCLI.cmd(
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


    private fun triggerEvent() =
        ServerCLI.cmd("event", "triggers random event") { options: List<String> ->
            if (options.size < 1) {
                System.err.println("event must be followed by a lobby id")
                return@cmd
            }
            val id = options[0].toInt()
            getGame(id)?.let { game ->
                RandomEventContainer().run(game)
            }
        }


    private fun nextTurn() =
        ServerCLI.cmd("nextturn", "Ends the current turn") { options: List<String> ->
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


    private fun gameInfo() =
        ServerCLI.cmd("game", "Info about a running game") { options: List<String> ->
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
                game.objects.forEach { print(">> $it") }
                game.allPlayers.forEach { player: Player ->
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
            val game = (lobby.data as? LobbyData)?.game
            if (game == null) {
                System.err.println("Lobby #$lobbyId has no active game!")
            } else {
                return game
            }
        }
        return null
    }

}


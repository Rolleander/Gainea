package com.broll.gainea.server.sites

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.net.NT_Battle_Reaction
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.utils.MessageUtils
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.IServerLobbyListener
import com.broll.networklib.server.impl.LobbyPlayer
import com.broll.networklib.server.impl.Player
import com.broll.networklib.server.impl.ServerLobby
import org.apache.commons.collections4.ListUtils
import java.util.Arrays
import java.util.Objects
import java.util.stream.Collectors

class LobbyListener : IServerLobbyListener<LobbyData, PlayerData> {
    override fun playerJoined(lobby: ServerLobby<LobbyData, PlayerData>, player: Player<PlayerData>) {
        var playerData = player.data
        if (playerData == null) {
            playerData = PlayerData()
            player.data = playerData
        }
        playerData.fraction = findOpenFraction(lobby)
    }

    private fun findOpenFraction(lobby: ServerLobby<LobbyData, PlayerData>): FractionType? {
        val takenFractions = lobby.players.stream().map { obj: LobbyPlayer<PlayerData> -> obj.data }
                .map { obj: PlayerData -> obj.fraction }.filter { obj: FractionType? -> Objects.nonNull(obj) }.collect(Collectors.toList())
        val allFractions = Arrays.asList(*FractionType.entries.toTypedArray())
        val remainingFractions = ListUtils.subtract(allFractions, takenFractions)
        var fraction = RandomUtils.pickRandom(remainingFractions)
        if (fraction == null) {
            fraction = RandomUtils.pickRandom(allFractions)
        }
        return fraction
    }

    override fun playerLeft(lobby: ServerLobby<LobbyData, PlayerData>, player: Player<PlayerData>) {}
    override fun playerDisconnected(lobby: ServerLobby<LobbyData, PlayerData>, player: Player<PlayerData>) {
        MessageUtils.gameLog(lobby.data.game, "Verbindung zu " + player.name + " verloren!")
        val game = lobby.data.game
        if (game != null) {
            if (game.battleHandler.isBattleActive) {
                //try retreat from fight so battle does not get stuck
                game.battleHandler.playerReaction(player.data.gamePlayer, NT_Battle_Reaction())
            }
        }
    }

    override fun playerReconnected(lobby: ServerLobby<LobbyData, PlayerData>, player: Player<PlayerData>) {
        val game = lobby.data.game
        game?.processingCore?.executeParallel({
            MessageUtils.gameLog(game, player.name + " ist zurück!")
            //send game reconnect update to reconnecting player
            val gamePlayer = player.data.gamePlayer
            player.sendTCP(game.reconnect(gamePlayer))
            ProcessingUtils.pause(1000)
            //check for open actions and resend them
            game.reactionHandler.playerReconnected(gamePlayer)
        }, 100)
    }

    override fun lobbyClosed(lobby: ServerLobby<LobbyData, PlayerData>) {}
}

package com.broll.gainea.server.sites

import com.broll.gainea.net.NT_Vote
import com.broll.gainea.net.NT_VotePending
import com.broll.gainea.net.NT_Vote_Base
import com.broll.gainea.net.NT_Vote_KickPlayer
import com.broll.gainea.net.NT_Vote_SkipTurn
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.displayMessage
import com.broll.gainea.server.core.utils.skipTurn
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.PackageReceiver
import com.broll.networklib.server.Autoshared
import com.broll.networklib.server.ConnectionRestriction
import com.broll.networklib.server.RestrictionType
import com.broll.networklib.server.ShareLevel
import com.broll.networklib.server.impl.ServerLobby
import kotlin.reflect.KClass

class GameVoteSite : GameSite() {

    private val voteActions = mapOf(
        action(NT_Vote_KickPlayer::class) {
            val player = lobby.getPlayer(vote.playerId)
            player.data.gamePlayer.surrender()
            lobby.kickPlayer(player)
            game.displayMessage(player.name + " musste aufgeben!", sound = "smash.ogg")
        },
        action(NT_Vote_SkipTurn::class) {
            game.skipTurn()
        }
    )

    private fun <T : NT_Vote_Base> action(type: KClass<T>, action: (ApplyVote<T>).() -> Unit) =
        (type to action) as Pair<KClass<*>, (ApplyVote<*>) -> Unit>

    private data class ApplyVote<T : NT_Vote_Base>(
        val vote: T,
        val lobby: ServerLobby<LobbyData, PlayerData>,
        val game: Game,
    )

    private inner class VoteContext {
        private var vote: NT_Vote_Base? = null
        private var initialPossibleVotes = 0
        val votes: MutableMap<Player, Boolean> = mutableMapOf()
        val pending: Boolean
            get() = vote != null

        fun reset() {
            vote = null
            votes.clear()
        }

        fun create(voteNt: NT_Vote_Base) {
            initialPossibleVotes = calcPossibleVotes()
            vote = voteNt
            votes.clear()
            val nt = NT_VotePending()
            nt.fromPlayerId = player.id
            nt.vote = voteNt
            lobby.sendToAllTCP(nt)
            vote(true)
        }

        fun vote(yes: Boolean) {
            votes.put(gamePlayer, yes)
            checkVotes()
        }

        private fun calcPossibleVotes() = game.activePlayers.count { !it.serverPlayer.isBot }

        private fun checkVotes() {
            val possibleVotes = Math.min(initialPossibleVotes, calcPossibleVotes())
            if (votes.size >= possibleVotes) {
                val yesVotes =
                    votes.filter { it.value && it.key.active && !it.key.serverPlayer.isBot }
                val nt = NT_Vote()
                nt.yes = yesVotes.size >= possibleVotes
                lobby.sendToAllTCP(nt)
                if (nt.yes) {
                    voteActions[vote!!::class]?.invoke(
                        ApplyVote(
                            vote!!, lobby, game
                        )
                    )
                }
                reset()
            }
        }
    }

    @Autoshared(ShareLevel.LOBBY)
    private lateinit var voting: VoteContext

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun kickPlayer(nt: NT_Vote_KickPlayer) {
        if (!voting.pending) {
            voting.create(nt)
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun skipTurn(nt: NT_Vote_SkipTurn) {
        if (!voting.pending) {
            voting.create(nt)
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun voted(nt: NT_Vote) {
        if (voting.pending) {
            voting.vote(nt.yes)
        }
    }


}
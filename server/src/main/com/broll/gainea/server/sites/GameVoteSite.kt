package com.broll.gainea.server.sites

import com.broll.gainea.net.proto.NT_Vote
import com.broll.gainea.net.proto.NT_VotePending
import com.broll.gainea.net.proto.NT_Vote_Base
import com.broll.gainea.net.proto.NT_Vote_KickPlayer
import com.broll.gainea.net.proto.NT_Vote_SkipTurn
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.displayMessage
import com.broll.gainea.server.core.utils.skipTurn
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.gainea.server.network.playerData
import com.broll.mpnll.server.lobby.Lobby
import com.broll.mpnll.server.site.PackageReceiver
import com.broll.mpnll.server.utils.Autoshared
import com.broll.mpnll.server.utils.ConnectionRestriction
import com.broll.mpnll.server.utils.RestrictionType
import com.broll.mpnll.server.utils.ShareLevel
import com.google.protobuf.Message
import kotlin.reflect.KClass

class GameVoteSite : GameSite() {

    private val voteActions = mapOf(
        action(NT_Vote_KickPlayer::class) {
            val player = lobby.findMember(vote.playerId)!!
            player.playerData.gamePlayer.surrender()
            lobby.removeUser(player, true)
            game.displayMessage(player.name + " musste aufgeben!", sound = "smash.ogg")
        },
        action(NT_Vote_SkipTurn::class) {
            game.skipTurn()
        }
    )

    private fun <T : Message> action(type: KClass<T>, action: (ApplyVote<T>).() -> Unit) =
        (type to action) as Pair<KClass<*>, (ApplyVote<*>) -> Unit>

    private data class ApplyVote<T : Message>(
        val vote: T,
        val lobby: Lobby,
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
            val nt = NT_VotePending.newBuilder()
                .setFromPlayerId(user.id)
                .setVote(voteNt)
                .build()
            lobby.sendToAll(nt)
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
                val nt = NT_Vote.newBuilder().setYes(yesVotes.size >= possibleVotes).build()
                lobby.sendToAll(nt)
                if (nt.yes) {
                    when (vote!!.valueCase) {
                        NT_Vote_Base.ValueCase.KICK_PLAYER -> applyVote(vote!!.kickPlayer)
                        NT_Vote_Base.ValueCase.SKIP_TURN -> applyVote(vote!!.skipTurn)
                        else -> Unit
                    }
                }
                reset()
            }
        }

        private fun <T : Message> applyVote(value: T) {
            @Suppress("UNCHECKED_CAST")
            (voteActions[value::class] as? ((ApplyVote<T>) -> Unit))
                ?.invoke(ApplyVote(value, lobby, game))
        }
    }

    @Autoshared(ShareLevel.LOBBY)
    private lateinit var voting: VoteContext

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun kickPlayer(nt: NT_Vote_KickPlayer) {
        if (!voting.pending) {
            voting.create(NT_Vote_Base.newBuilder().setKickPlayer(nt).build())
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_LOCKED)
    fun skipTurn(nt: NT_Vote_SkipTurn) {
        if (!voting.pending) {
            voting.create(NT_Vote_Base.newBuilder().setSkipTurn(nt).build())
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

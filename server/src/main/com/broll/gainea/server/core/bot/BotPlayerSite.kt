package com.broll.gainea.server.core.bot

import com.broll.gainea.net.proto.NT_Battle_Reaction
import com.broll.gainea.net.proto.NT_EndTurn
import com.broll.gainea.net.proto.NT_LoadedGame
import com.broll.gainea.net.proto.NT_Reaction
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.message
import com.broll.gainea.server.core.battle.BattleHandler
import com.broll.gainea.server.core.bot.impl.BotAttack
import com.broll.gainea.server.core.bot.strategy.BotStrategy
import com.broll.gainea.server.core.bot.strategy.StrategyConstants
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.endTurn
import com.broll.gainea.server.network.gameData
import com.broll.gainea.server.network.playerData
import com.broll.mpnll.server.lobby.Lobby
import com.broll.mpnll.server.user.User
import com.google.protobuf.Message
import org.slf4j.LoggerFactory

/** In-process counterpart of a network client for server-controlled players. */
class BotPlayerSite(private val lobby: Lobby, private val bot: User) {
    private var battleAttackers: List<Unit> = listOf()
    private var battleDefenders: List<Unit> = listOf()
    private lateinit var botActionHandler: BotActionHandler
    private var allowRetreat = false
    private lateinit var strategy: BotStrategy

    private val game: Game
        get() = lobby.gameData.game!!

    fun receive(message: Message) {
        when (message) {
            is com.broll.gainea.net.proto.NT_StartGame -> gameStart()
            is NT_EndTurn -> sendServer(NT_EndTurn.getDefaultInstance())
            is com.broll.gainea.net.proto.NT_PlayerTurnActions -> turnActions(message)
            is com.broll.gainea.net.proto.NT_PlayerAction -> handleAction(message)
            is com.broll.gainea.net.proto.NT_Battle_Start -> battleStart(message)
            is com.broll.gainea.net.proto.NT_Battle_Update -> battleUpdate(message)
            is com.broll.gainea.net.proto.NT_Event_ReceivedGoal -> strategy.synchronizeGoalStrategies()
            is com.broll.gainea.net.proto.NT_Event_FinishedGoal -> if (message.base.player == bot.id) strategy.synchronizeGoalStrategies()
        }
    }

    private fun gameStart() {
        Log.info("$bot Bot loaded game")
        val player = bot.playerData.gamePlayer
        strategy = BotStrategy(game, player, StrategyConstants())
        botActionHandler = BotActionHandler(game, player, strategy)
        sendServer(NT_LoadedGame.getDefaultInstance())
    }

    private fun turnActions(message: com.broll.gainea.net.proto.NT_PlayerTurnActions) {
        strategy.prepareTurn()
        sendServer(botActionHandler.createBestReaction(message))
    }

    private fun handleAction(message: com.broll.gainea.net.proto.NT_PlayerAction) {
        sendServer(botActionHandler.react(message.action.message()))
    }

    private fun battleStart(message: com.broll.gainea.net.proto.NT_Battle_Start) {
        allowRetreat = message.allowRetreat && message.attacker == bot.id
        battleAttackers = BotUtils.getObjects(game, message.attackersList)
        battleDefenders = BotUtils.getObjects(game, message.defendersList)
    }

    private fun battleUpdate(message: com.broll.gainea.net.proto.NT_Battle_Update) {
        ProcessingUtils.pause(
            BattleHandler.getAnimationDelay(
                message.attackerRollsCount,
                message.defenderRollsCount
            )
        )
        if (allowRetreat) {
            val attack = botActionHandler.getActionHandler(BotAttack::class.java)
            sendServer(
                NT_Battle_Reaction.newBuilder()
                    .setKeepAttacking(attack.keepAttacking(battleAttackers, battleDefenders))
                    .build()
            )
        }
    }

    private fun sendServer(message: Any) {
        val player = bot.playerData.gamePlayer
        when (message) {
            is NT_LoadedGame -> lobby.gameData.playerLoaded?.invoke(bot)
            is NT_EndTurn -> if (game.currentPlayer === player) game.endTurn()
            is NT_Battle_Reaction -> game.battleHandler.playerReaction(player, message)
            is NT_Reaction -> {
                val action = game.getAction(message.actionId) ?: return
                game.reactionHandler.handle(player, action, message)
            }
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BotPlayerSite::class.java)
    }
}

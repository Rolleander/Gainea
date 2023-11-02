package com.broll.gainea.server.core.bot

import com.broll.gainea.net.NT_Battle_Reaction
import com.broll.gainea.net.NT_Battle_Start
import com.broll.gainea.net.NT_Battle_Update
import com.broll.gainea.net.NT_EndTurn
import com.broll.gainea.net.NT_Event_FinishedGoal
import com.broll.gainea.net.NT_Event_ReceivedGoal
import com.broll.gainea.net.NT_LoadedGame
import com.broll.gainea.net.NT_PlayerAction
import com.broll.gainea.net.NT_PlayerTurnActions
import com.broll.gainea.net.NT_StartGame
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleHandler
import com.broll.gainea.server.core.bot.impl.BotAttack
import com.broll.gainea.server.core.bot.strategy.BotStrategy
import com.broll.gainea.server.core.bot.strategy.StrategyConstants
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.PackageReceiver
import com.broll.networklib.server.impl.BotSite
import org.slf4j.LoggerFactory

class BotPlayerSite : BotSite<PlayerData>() {
    private var battleAttackers: List<Unit> = listOf()
    private var battleDefenders: List<Unit> = listOf()
    private lateinit var botActionHandler: BotActionHandler
    private var allowRetreat = false
    private lateinit var strategy: BotStrategy

    val game: Game
        get() = (bot.serverLobby.data as LobbyData).game!!

    @PackageReceiver
    fun gameStart(start: NT_StartGame) {
        Log.info(bot.toString() + " Bot send loaded Game!")
        sendServer(NT_LoadedGame())
        val player = bot.data.gamePlayer
        strategy = BotStrategy(game, player, StrategyConstants())
        botActionHandler = BotActionHandler(game, player, strategy)
    }

    @PackageReceiver
    fun endTurn(nt: NT_EndTurn) {
        //can only end turn, does not wait
        sendServer(NT_EndTurn())
    }

    @PackageReceiver
    fun turnActions(turn: NT_PlayerTurnActions) {
        Log.trace("$bot turn")
        strategy.prepareTurn()
        sendServer(botActionHandler.createBestReaction(turn))
    }

    @PackageReceiver
    fun handleAction(requiredAction: NT_PlayerAction) {
        sendServer(botActionHandler.react(requiredAction.action))
    }

    @PackageReceiver
    fun battleStart(start: NT_Battle_Start) {
        allowRetreat = start.allowRetreat && start.attacker == bot.id
        battleAttackers = BotUtils.getObjects(game, start.attackers)
        battleDefenders = BotUtils.getObjects(game, start.defenders)
    }

    @PackageReceiver
    fun battleUpdate(update: NT_Battle_Update) {
        ProcessingUtils.pause(
            BattleHandler.getAnimationDelay(
                update.attackerRolls.size,
                update.defenderRolls.size
            )
        )
        if (allowRetreat) {
            val attack = botActionHandler.getActionHandler(BotAttack::class.java)
            val nt = NT_Battle_Reaction()
            nt.keepAttacking = attack.keepAttacking(battleAttackers, battleDefenders)
            sendServer(nt)
        }
    }

    @PackageReceiver
    fun newGoal(nt: NT_Event_ReceivedGoal) {
        strategy.synchronizeGoalStrategies()
    }

    @PackageReceiver
    fun finishedGoal(nt: NT_Event_FinishedGoal) {
        if (nt.player == bot.id) {
            strategy.synchronizeGoalStrategies()
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BotPlayerSite::class.java)
    }
}

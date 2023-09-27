package com.broll.gainea.server.core.bot

import com.broll.gainea.net.NT_Battle_Reactionimport

com.broll.gainea.net.NT_Battle_Startimport com.broll.gainea.net.NT_Battle_Updateimport com.broll.gainea.net.NT_EndTurnimport com.broll.gainea.net.NT_Event_FinishedGoalimport com.broll.gainea.net.NT_Event_ReceivedGoalimport com.broll.gainea.net.NT_LoadedGameimport com.broll.gainea.net.NT_PlayerActionimport com.broll.gainea.net.NT_PlayerTurnActionsimport com.broll.gainea.net.NT_StartGameimport com.broll.gainea.server.core.bot.impl .BotAttackimport com.broll.gainea.server.core.bot.strategy.BotStrategyimport com.broll.gainea.server.core.bot.strategy.StrategyConstantsimport com.broll.gainea.server.core.utils.ProcessingUtilsimport com.broll.gainea.server.init.LobbyDataimport com.broll.gainea.server.init.PlayerDataimport com.broll.networklib.PackageReceiverimport com.broll.networklib.server.impl .BotSite com.broll.gainea.server.core.battle.BattleHandler
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListenerimport

org.slf4j.LoggerFactory
class BotPlayerSite : BotSite<PlayerData?>() {
    private var botActionHandler: BotActionHandler? = null
    private var allowRetreat = false
    private var strategy: BotStrategy? = null
    @PackageReceiver
    fun gameStart(start: NT_StartGame?) {
        Log.info(bot.toString() + " Bot send loaded Game!")
        sendServer(NT_LoadedGame())
        val game = (bot.serverLobby.data as LobbyData).game
        val player = bot.data.getGamePlayer()
        strategy = BotStrategy(game, player, StrategyConstants())
        botActionHandler = BotActionHandler(game, player, strategy!!)
    }

    @PackageReceiver
    fun endTurn(nt: NT_EndTurn?) {
        //can only end turn, does not wait
        sendServer(NT_EndTurn())
    }

    @PackageReceiver
    fun turnActions(turn: NT_PlayerTurnActions) {
        Log.trace("$bot turn")
        strategy!!.prepareTurn()
        sendServer(botActionHandler!!.createBestReaction(turn))
    }

    @PackageReceiver
    fun handleAction(requiredAction: NT_PlayerAction) {
        sendServer(botActionHandler!!.react(requiredAction.action))
    }

    @PackageReceiver
    fun battleStart(start: NT_Battle_Start) {
        allowRetreat = start.allowRetreat && start.attacker == bot.id
    }

    @PackageReceiver
    fun battleUpdate(update: NT_Battle_Update) {
        ProcessingUtils.pause(BattleHandler.Companion.getAnimationDelay(update.attackerRolls.size, update.defenderRolls.size))
        if (allowRetreat) {
            val attack = botActionHandler!!.getActionHandler(BotAttack::class.java) as BotAttack
            val nt = NT_Battle_Reaction()
            nt.keepAttacking = attack.keepAttacking(update)
            sendServer(nt)
        }
    }

    @PackageReceiver
    fun newGoal(nt: NT_Event_ReceivedGoal?) {
        strategy!!.synchronizeGoalStrategies()
    }

    @PackageReceiver
    fun finishedGoal(nt: NT_Event_FinishedGoal) {
        if (nt.player == bot.id) {
            strategy!!.synchronizeGoalStrategies()
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BotPlayerSite::class.java)
    }
}

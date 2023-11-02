package com.broll.gainea.server.core

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_BoardUpdate
import com.broll.gainea.net.NT_ReconnectGame
import com.broll.gainea.net.NT_StartGame
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.actions.ReactionActions
import com.broll.gainea.server.core.actions.ReactionHandler
import com.broll.gainea.server.core.actions.TurnBuilder
import com.broll.gainea.server.core.battle.BattleHandler
import com.broll.gainea.server.core.cards.CardStorage
import com.broll.gainea.server.core.goals.GoalStorage
import com.broll.gainea.server.core.map.MapContainer
import com.broll.gainea.server.core.objects.MapEffect
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.buffs.BuffProcessor
import com.broll.gainea.server.core.objects.monster.MonsterFactory
import com.broll.gainea.server.core.player.NeutralPlayer
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.PlayerFactory
import com.broll.gainea.server.core.processing.GameUpdateReceiverProxy
import com.broll.gainea.server.core.processing.ProcessingCore
import com.broll.gainea.server.core.stats.GameStatistic
import com.broll.gainea.server.core.utils.isGameEnd
import com.broll.gainea.server.core.utils.noActivePlayersRemaining
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.ServerLobby
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

class Game(val lobby: ServerLobby<LobbyData, PlayerData>) {
    val neutralPlayer = NeutralPlayer(this)
    val map: MapContainer
    val allPlayers: List<Player>
    val objects = mutableListOf<MapObject>()
    val effects = mutableListOf<MapEffect>()
    private val actions = HashMap<Int, ActionContext<out NT_Action>>()
    var rounds = 1
        private set
    var currentTurn = -1
        private set
    private val actionCounter = AtomicInteger()
    private val boardObjectCounter = AtomicInteger()

    lateinit var reactionHandler: ReactionHandler

    lateinit var turnBuilder: TurnBuilder

    lateinit var battleHandler: BattleHandler

    lateinit var goalStorage: GoalStorage

    lateinit var cardStorage: CardStorage
    lateinit var processingCore: ProcessingCore

    val monsterFactory: MonsterFactory
    val updateReceiver: GameUpdateReceiverProxy
    val buffProcessor: BuffProcessor
    val statistic: GameStatistic
    var isGameOver = false
        private set

    init {
        lobby.data.game = this
        updateReceiver = GameUpdateReceiverProxy()
        updateReceiver.register(TurnEvents(this))
        map = MapContainer(lobby.data.expansionSetting)
        allPlayers = PlayerFactory.create(this, lobby.players)
        monsterFactory = MonsterFactory()
        buffProcessor = BuffProcessor(this)
        statistic = GameStatistic(this)
        updateReceiver.register(statistic)
    }

    fun initHandlers(reactionResult: ReactionActions) {
        val actionHandlers = ActionHandlers(this, reactionResult)
        reactionHandler = ReactionHandler(this, actionHandlers)
        processingCore = ProcessingCore(this, { reactionHandler.finishedProcessing() }, lobby)
        turnBuilder = TurnBuilder(this, actionHandlers)
        battleHandler = BattleHandler(this, reactionResult)
        goalStorage = GoalStorage(this, lobby.data.goalTypes)
        cardStorage = CardStorage(this)
    }


    fun newObjectId() = boardObjectCounter.incrementAndGet()

    @Synchronized
    fun pushAction(action: ActionContext<out NT_Action>) {
        val nr = actionCounter.incrementAndGet()
        action.action.actionId = nr
        actions[nr] = action
    }

    @Synchronized
    fun getAction(id: Int): ActionContext<out NT_Action>? {
        return actions[id]
    }

    @Synchronized
    fun consumeAction(id: Int) {
        actions.remove(id)
    }

    fun clearActions() {
        actions.clear()
    }

    fun start(player: Player): NT_StartGame {
        val nt = NT_StartGame()
        fillUpdate(nt)
        nt.expansionsSetting = map.expansionSetting.ordinal
        nt.pointLimit = lobby.data.pointLimit
        nt.roundLimit = lobby.data.roundLimit
        nt.cards = player.cardHandler.ntCards()
        nt.shop = player.mercenaryShop.nt()
        return nt
    }

    fun end() {
        Log.trace("Gamend called")
        if (!isGameOver) {
            statistic.registerRound()
            isGameOver = true
        }
    }

    fun reconnect(player: Player): NT_ReconnectGame {
        val nt = NT_ReconnectGame()
        fillUpdate(nt)
        nt.expansionsSetting = map.expansionSetting.ordinal
        nt.cards = player.cardHandler.ntCards()
        nt.goals = player.goalHandler.ntGoals()
        nt.pointLimit = lobby.data.pointLimit
        nt.roundLimit = lobby.data.roundLimit
        nt.shop = player.mercenaryShop.nt()
        return nt
    }

    fun fillUpdate(update: NT_BoardUpdate) {
        update.round = rounds.toShort()
        update.turn = currentTurn.toShort()
        update.players = allPlayers.map { it.nt() }.toTypedArray()
        update.objects = objects.map { it.nt() }.toTypedArray()
        update.effects = effects.map { it.nt() }.toTypedArray()
    }

    fun nt(): NT_BoardUpdate {
        val board = NT_BoardUpdate()
        fillUpdate(board)
        return board
    }

    @Synchronized
    fun nextTurn(): Player? {
        actions.clear()
        currentTurn++
        if (noActivePlayersRemaining()) {
            return null
        }
        if (currentTurn >= allPlayers.size) {
            currentTurn = 0
            rounds++
            if (isGameEnd()) {
                return null
            }
        }
        return allPlayers[currentTurn]
    }

    val activePlayers: List<Player>
        get() = allPlayers.filter { it.active }
    val currentPlayer: Player
        get() = allPlayers[currentTurn]

    fun isPlayersTurn(player: Player) = allPlayers.indexOf(player) == currentTurn


    val gameSettings: LobbyData
        get() = lobby.data

    companion object {
        private val Log = LoggerFactory.getLogger(Game::class.java)
    }
}

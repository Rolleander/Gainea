package com.broll.gainea.server.core

import com.broll.gainea.net.proto.NT_BoardUpdate
import com.broll.gainea.net.proto.NT_GameOver
import com.broll.gainea.net.proto.NT_ReconnectGame
import com.broll.gainea.net.proto.NT_StartGame
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
import com.broll.gainea.server.network.gameData
import com.broll.gainea.server.network.players
import com.broll.mpnll.server.lobby.Lobby
import com.google.protobuf.Message
import org.slf4j.LoggerFactory
import java.util.concurrent.atomic.AtomicInteger

class Game(val lobby: Lobby) {
    val neutralPlayer = NeutralPlayer(this)
    val map: MapContainer
    val allPlayers: List<Player>
    val objects = mutableListOf<MapObject>()
    val effects = mutableListOf<MapEffect>()
    private val actions = HashMap<Int, ActionContext<out Message>>()
    var rounds = 1
        private set
    var currentTurn = -1
        private set
    private val actionCounter = AtomicInteger()
    private val boardObjectCounter = AtomicInteger()

    lateinit var reactionHandler: ReactionHandler

    lateinit var turnBuilder: TurnBuilder

    lateinit var battleHandler: BattleHandler

    val goalStorage: GoalStorage
    val cardStorage: CardStorage
    lateinit var processingCore: ProcessingCore

    val monsterFactory: MonsterFactory
    val updateReceiver: GameUpdateReceiverProxy
    val buffProcessor: BuffProcessor
    val statistic: GameStatistic
    var isGameOver = false
        private set

    init {
        updateReceiver = GameUpdateReceiverProxy()
        updateReceiver.register(TurnEvents(this))
        map = MapContainer(lobby.gameData.expansionSetting)
        allPlayers = PlayerFactory.create(this, lobby.players)
        monsterFactory = MonsterFactory()
        buffProcessor = BuffProcessor(this)
        statistic = GameStatistic(this)
        updateReceiver.register(statistic)
        goalStorage = GoalStorage(this, lobby.gameData.goalTypes)
        cardStorage = CardStorage(this)
    }

    fun initHandlers(reactionResult: ReactionActions) {
        val actionHandlers = ActionHandlers(this, reactionResult)
        reactionHandler = ReactionHandler(this, actionHandlers)
        processingCore = ProcessingCore(this, { reactionHandler.finishedProcessing() }, lobby)
        turnBuilder = TurnBuilder(this, actionHandlers)
        battleHandler = BattleHandler(this, reactionResult)
    }

    fun newObjectId() = boardObjectCounter.incrementAndGet()

    @Synchronized
    fun pushAction(action: ActionContext<out Message>) {
        val nr = actionCounter.incrementAndGet()
        action.assignActionId(nr)
        actions[nr] = action
    }

    @Synchronized
    fun getAction(id: Int): ActionContext<out Message>? {
        return actions[id]
    }

    @Synchronized
    fun consumeAction(id: Int) {
        actions.remove(id)
    }

    fun clearActions() {
        actions.clear()
    }

    fun start(player: Player): NT_StartGame =
        NT_StartGame.newBuilder()
            .setUpdate(nt())
            .setPointLimit(lobby.gameData.pointLimit).setRoundLimit(lobby.gameData.roundLimit)
            .addAllCards(player.cardHandler.ntCards()).setShop(player.shop.nt())
            .addAllExpansions(map.expansions.map { it.nt() }).build()

    fun end() {
        Log.trace("Gamend called")
        if (!isGameOver) {
            statistic.registerRound()
            isGameOver = true
        }
    }

    fun reconnect(player: Player): NT_ReconnectGame =
        NT_ReconnectGame.newBuilder()
            .setStart(start(player))
            .addAllGoals(player.goalHandler.ntGoals()).build()

    fun nt(): NT_BoardUpdate = NT_BoardUpdate.newBuilder().setRound(rounds).setTurn(currentTurn)
        .addAllPlayers(allPlayers.map { it.nt() }).addAllObjects(objects.map { it.boardNt() })
        .addAllEffects(effects.map { it.nt() }).build()


    fun gameOver(): NT_GameOver =
        NT_GameOver.newBuilder().setUpdate(nt()).build()

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
        get() = lobby.gameData

    companion object {
        private val Log = LoggerFactory.getLogger(Game::class.java)
    }
}

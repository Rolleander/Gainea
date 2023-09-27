package com.broll.gainea.server.core.processing

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.init.LobbyData
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.ServerLobby
import com.google.common.util.concurrent.ThreadFactoryBuilder
import org.slf4j.LoggerFactory
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class ProcessingCore(private val game: GameContainer, private val finishedProcessingListener: Runnable, lobby: ServerLobby<LobbyData, PlayerData>) {
    private val executor: ScheduledExecutorService
    private val parallelExecutor: ScheduledExecutorService
    private var lastFuture: ScheduledFuture<*>? = null
    private val queue: Queue<RunnableWrapper> = ConcurrentLinkedQueue()

    init {
        val id = lobby.id
        executor = Executors.newScheduledThreadPool(1, ThreadFactoryBuilder().setNameFormat("gainea-$id").build())
        parallelExecutor = Executors.newScheduledThreadPool(3, ThreadFactoryBuilder().setNameFormat("gainea-$id-%d").build())
    }

    fun executeParallel(runnable: Runnable) {
        executeParallel(runnable, 0)
    }

    @Synchronized
    fun executeParallel(runnable: Runnable, afterDelay: Int) {
        parallelExecutor.schedule(runnable, afterDelay.toLong(), TimeUnit.MILLISECONDS)
    }

    fun execute(runnable: Runnable) {
        execute(runnable, 50)
    }

    @Synchronized
    fun execute(runnable: Runnable, afterDelay: Int) {
        var delay = afterDelay
        if (game.isGameOver) {
            return
        }
        if (ProcessingUtils.MAX_PAUSE > 0) {
            delay = Math.min(delay, ProcessingUtils.MAX_PAUSE)
        }
        val wrapper = RunnableWrapper(runnable, delay)
        if (isBusy) {
            //add to queue
            queue.add(wrapper)
        } else {
            //execute directly
            execute(wrapper)
        }
    }

    private fun execute(wrapper: RunnableWrapper) {
        lastFuture = executor.schedule(wrapper, wrapper.delay.toLong(), TimeUnit.MILLISECONDS)
    }

    @get:Synchronized
    val isBusy: Boolean
        get() = if (lastFuture == null) {
            false
        } else !lastFuture!!.isDone || !queue.isEmpty()

    @Synchronized
    private fun done() {
        val wrapper = queue.poll()
        if (wrapper != null && !executor.isShutdown) {
            //start next one
            execute(wrapper)
        } else {
            //done with all
            finishedProcessingListener.run()
        }
    }

    fun shutdown() {
        executor.shutdownNow()
        parallelExecutor.shutdownNow()
    }

    private inner class RunnableWrapper(var runnable: Runnable,
                                        var delay : Int = 0 ) : Runnable {

        override fun run() {
            try {
                runnable.run()
            } catch (e: Exception) {
                Log.error("Processing exception:", e)
            }
            done()
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(ProcessingCore::class.java)
    }
}

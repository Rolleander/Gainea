package com.broll.gainea.server.core.processing

import com.broll.gainea.server.core.player.Player
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ProcessingBlock {
    var playerLeft = false
        private set

    private var future: CompletableFuture<Any?>? = null

    @Synchronized
    fun prepare() {
        future = CompletableFuture()
    }

    fun waitFor(player: Player) {
        playerLeft = false
        val waitingFuture = synchronized(this) {
            future ?: CompletableFuture<Any?>().also { future = it }
        }
        var recheck: Boolean
        do {
            recheck = false
            try {
                waitingFuture.get(INACTIVE_CHECK_INTERVAL.toLong(), TimeUnit.MILLISECONDS)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                Log.error("Failed waiting for processing block", e)
            } catch (e: ExecutionException) {
                Log.error("Failed waiting for processing block", e)
            } catch (e: TimeoutException) {
                recheck = player.active
                playerLeft = !player.active
            }
        } while (recheck)
        synchronized(this) {
            if (future === waitingFuture) {
                future = null
            }
        }
    }

    @Synchronized
    fun resume() {
        future?.complete(null)
    }

    companion object {
        private const val INACTIVE_CHECK_INTERVAL = 500
        private val Log = LoggerFactory.getLogger(ProcessingBlock::class.java)
    }
}

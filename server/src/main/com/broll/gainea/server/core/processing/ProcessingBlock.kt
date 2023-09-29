package com.broll.gainea.server.core.processing

import com.broll.gainea.server.core.player.Player
import org.slf4j.LoggerFactory
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class ProcessingBlock {
    private var future: CompletableFuture<*>? = null
    fun waitFor(player: Player) {
        var recheck: Boolean
        do {
            recheck = false
            try {
                future = CompletableFuture<Any?>()
                future!!.get(INACTIVE_CHECK_INTERVAL.toLong(), TimeUnit.MILLISECONDS)
            } catch (e: InterruptedException) {
                Log.error("Failed waiting for processing block", e)
            } catch (e: ExecutionException) {
                Log.error("Failed waiting for processing block", e)
            } catch (e: TimeoutException) {
                recheck = player.active
            }
        } while (recheck)
    }

    fun resume() {
        future!!.complete(null)
    }

    companion object {
        private const val INACTIVE_CHECK_INTERVAL = 500
        private val Log = LoggerFactory.getLogger(ProcessingBlock::class.java)
    }
}

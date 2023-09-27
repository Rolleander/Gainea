package com.broll.gainea.server.core.utils

import org.slf4j.LoggerFactory

object ProcessingUtils {
    private val Log = LoggerFactory.getLogger(ProcessingUtils::class.java)
    private const val DEFAULT_PAUSE = 1000
    var MAX_PAUSE = 0
    @JvmOverloads
    fun pause(ms: Int = DEFAULT_PAUSE) {
        var ms = ms
        if (MAX_PAUSE > 0) {
            ms = Math.min(MAX_PAUSE, ms)
        }
        if (ms > 0) {
            try {
                Thread.sleep(ms.toLong())
            } catch (e: InterruptedException) {
                throw RuntimeException("shutdown")
            }
        }
    }
}

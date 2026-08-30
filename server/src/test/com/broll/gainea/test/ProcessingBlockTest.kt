package com.broll.gainea.test

import com.broll.gainea.server.core.processing.ProcessingBlock
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTimeout
import org.junit.jupiter.api.Test
import java.time.Duration

class ProcessingBlockTest {
    @Test
    fun `response before wait is not lost`() {
        val player = testGame(playersCount = 1).allPlayers.first()
        val block = ProcessingBlock()
        block.prepare()
        block.resume()

        assertTimeout(Duration.ofMillis(100)) {
            block.waitFor(player)
        }
        assertFalse(block.playerLeft)
    }
}

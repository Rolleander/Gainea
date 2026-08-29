package com.broll.gainea.test

import com.broll.gainea.server.core.battle.UnitSnapshot
import com.broll.gainea.server.core.objects.Soldier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class PoacherTest {

    @Test
    fun test() {
        val game = testGame()
        val unit = Soldier(game.neutralPlayer)
        unit.setStats(5, 5)

        val snapshot = UnitSnapshot(unit)

        unit.takeDamage(5)

        Assertions.assertEquals(true, unit.dead)
        Assertions.assertEquals(false, snapshot.dead)

    }

}

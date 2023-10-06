package com.broll.gainea.test

import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.buffs.BuffType.ADD
import com.broll.gainea.server.core.objects.buffs.BuffType.MULTIPLY
import com.broll.gainea.server.core.objects.buffs.IntBuff
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class BuffTest {

    val game = testGame()

    @Test
    fun `buffs apply correctly`() {

        val unit = Soldier(game.neutralPlayer)
        unit.setStats(2, 2)

        unit.addHealth(2)

        assertEquals(4, unit.health.value)

        unit.health.value += 2

        assertEquals(6, unit.health.value)

        unit.health.value = unit.health.rootValue * 2
        assertEquals(12, unit.health.value)

        val buff = IntBuff(MULTIPLY, 2)
        unit.addHealthBuff(buff)
        assertEquals(24, unit.health.value)

        unit.addHealth(-2)

        assertEquals(20, unit.health.value)

        val buff2 = IntBuff(ADD, 4)
        unit.addHealthBuff(buff2)
        assertEquals(24, unit.health.value)

        buff.remove()

        assertEquals(14, unit.health.value)

        buff2.remove()

        assertEquals(10, unit.health.value)

    }

}
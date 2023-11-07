package com.broll.gainea.test

import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.impl.GuardsFraction
import com.broll.gainea.server.core.map.impl.GaineaMap.Areas.XOMDELTA
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.DummyLobbyPlayer
import org.amshove.kluent.`should be equal to`
import org.junit.jupiter.api.Test

class FractionsTest {
    val game = testGame()
    private fun initFraction(f: Fraction): Fraction {
        val lp = DummyLobbyPlayer<PlayerData>()
        lp.data = PlayerData(f.type)
        Player(game, f, lp)
        return f
    }

    @Test
    fun `guard buff works`() {
        val f = initFraction(GuardsFraction())
        val s = f.createSoldier()
        s.prepareForTurnStart()
        s.numberPlus.value `should be equal to` 0
        s.prepareForTurnStart()
        s.numberPlus.value `should be equal to` 1
        s.location = game.map.getArea(XOMDELTA)!!
        s.prepareForTurnStart()
        s.numberPlus.value `should be equal to` 0
    }

}
package com.broll.gainea.server.core.player

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType.DRUIDS
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.init.PlayerData
import com.broll.networklib.server.impl.DummyLobbyPlayer

class NeutralPlayer(game: Game) : Player(game, NeutralFraction, NeutralServerPlayer) {
    init {
        fraction.init(game, this)
    }
}

fun Player.isNeutral() = this is NeutralPlayer

object NeutralFraction : Fraction(type = DRUIDS) {

    override val description: FractionDescription
        get() = throw RuntimeException("invalid for neutral")

    override fun isHostile(unit: Unit): Boolean = true

    override fun createSoldier(): Soldier {
        throw RuntimeException("invalid for neutral")
    }

    override fun createCommander(): Soldier {
        throw RuntimeException("invalid for neutral")
    }


}

object NeutralServerPlayer : DummyLobbyPlayer<PlayerData>(-1) {

    init {
        data = PlayerData(DRUIDS)
    }
}


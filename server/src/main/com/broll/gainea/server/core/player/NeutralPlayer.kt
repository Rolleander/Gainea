package com.broll.gainea.server.core.player

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType.DRUIDS
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.init.PlayerData

class NeutralPlayer(game: Game) : Player(game, NeutralFraction, NeutralServerPlayer)

fun Player.isNeutral() = this is NeutralPlayer

object NeutralFraction : Fraction(type = DRUIDS) {

    override fun description(): FractionDescription {
        val dummyDescription = UnitDescription("", 0, 0, 0)
        return FractionDescription("", dummyDescription, dummyDescription)
    }

    override fun createSoldier(): Soldier {
        throw RuntimeException("invalid for neutral")
    }

    override fun createCommander(): Soldier {
        throw RuntimeException("invalid for neutral")
    }

}

object NeutralServerPlayer : com.broll.networklib.server.impl.LobbyPlayer<PlayerData>(DummyPlayer) {
    override fun isOnline() = false
}

object DummyPlayer : com.broll.networklib.server.impl.Player<PlayerData>(-1, "", null) {
    init {
        data = PlayerData(DRUIDS)
    }
}


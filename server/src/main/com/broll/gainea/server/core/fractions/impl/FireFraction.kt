package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.UnitControl.damage
import com.broll.gainea.server.core.utils.isAreaType
import com.broll.gainea.server.core.utils.selectHostileUnit

class FireFraction : Fraction(FractionType.FIRE) {
    private var turns = 0

    override val description: FractionDescription = FractionDescription(
        "",
        soldier = UnitDescription(name = "Feuermagier", icon = 23),
        commander = UnitDescription(
            name = "Flammenschürer Duras",
            icon = 48,
            power = 3,
            health = 3
        ),
    ).apply {
        plus("Erhält jede dritte Runde eine Feuerregen-Karte\n(Verursacht 1 Schaden an einer beliebigen feindlichen Einheit)")
        plus("Zahl +1 auf Wüsten")
        contra("Erhält keine Belohnung für besiegte Monster auf Schnee oder Seen")
    }

    override fun powerMutatorArea(power: FightingPower, area: Area) {
        if (area.type == AreaType.DESERT) {
            power.changeNumberPlus(1)
        }
    }

    override fun prepareTurn() {
        turns++
        if (turns == 3) {
            owner.cardHandler.receiveCard(FireRain())
            turns = 0
        }
        super.prepareTurn()
    }

    override fun killedMonster(monster: Monster, battleResult: BattleResult) {
        if (monster.location.isAreaType(AreaType.SNOW, AreaType.LAKE)) {
            //no card when monster on ice or water
            return
        }
        super.killedMonster(monster, battleResult)
    }

}

class FireRain :
    Card(76, DISRUPTION, "Feuerregen", "Verursacht 1 Schaden an einer beliebigen Einheit") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val unit = game.selectHostileUnit(owner, "Welcher Einheit soll Schaden zugeführt werden?")
        if (unit != null) {
            game.damage(unit)
        }
    }
}

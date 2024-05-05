package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.UnitControl.focus
import com.broll.gainea.server.core.utils.isAreaType

class SpartanFraction : Fraction(FractionType.SPARTANS) {

    override val description: FractionDescription = FractionDescription(
        "",
        soldier = UnitDescription(name = "Spartaner", icon = 139),
        commander = UnitDescription(
            name = "Leonidas",
            icon = 133,
            power = 3,
            health = 3
        ),
    ).apply {
        plus("Jede Runde erhält eine Einheit +1 Angriff")
        plus("In Unterzahl +1 Zahl")
        contra("Auf Schnee und Seen -1 Zahl")
        contra("Besiegte Monster geben nur noch die Hälfte der Sterne (abgerundet)")
    }

    override fun prepareTurn() {
        owner.units.randomOrNull()?.let {
            it.power.value += 1
            game.focus(it, NT_Event.EFFECT_BUFF)
        }
        super.prepareTurn()
    }


    override fun killedMonster(monster: Monster, battleResult: BattleResult) {
        owner.goalHandler.addStars(monster.stars / 2)
        if (monster.owner.isNeutral()) {
            owner.cardHandler.drawRandomCard()
        }
    }

    override fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = super.calcFightingPower(soldier, context)
        val army = context.getFightingArmy(soldier)
        val opponents = context.getOpposingFightingArmy(soldier)
        if (army.size < opponents.size) {
            // smaller army +1 Z
            power.changeNumberPlus(1)
        }
        if (context.location.isAreaType(AreaType.SNOW, AreaType.LAKE)) {
            power.changeNumberPlus(-1)
        }
        return power
    }

}

package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.UnitDescription
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.UnitControl.damage
import com.broll.gainea.server.core.utils.isAreaType
import com.broll.gainea.server.core.utils.selectHostileUnit

class FireFraction : Fraction(FractionType.FIRE) {
    private var turns = 0
    override fun description(): FractionDescription {
        val desc = FractionDescription(
            "",
            soldier = UnitDescription(name = "Feuermagier", icon = 23),
            commander = UnitDescription(
                name = "Flammenschürer Duras",
                icon = 48,
                power = 3,
                health = 3
            ),
        )
        desc.plus("Erhält jede dritte Runde eine Feuerregen-Karte\n(Verursacht 1 Schaden an einer beliebigen feindlichen Einheit)")
        desc.plus("Zahl +1 auf Wüsten")
        desc.contra("Erhält keine Belohnung für besiegte Monster auf Schnee oder Seen")
        return desc
    }

    override fun powerMutatorArea(power: FightingPower, area: Area) {
        if (area.type == AreaType.DESERT) {
            power.changeNumberPlus(1)
        }
    }

    override fun prepareTurn(actionHandlers: ActionHandlers) {
        turns++
        if (turns == 3) {
            owner.cardHandler.receiveCard(FireRain())
            turns = 0
        }
        super.prepareTurn(actionHandlers)
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner, fraction = this)
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH)
        soldier.name = "Feuermagier"
        soldier.icon = 23
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner, fraction = this)
        commander.isCommander = true
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH)
        commander.name = "Flammenschürer Duras"
        commander.icon = 48
        return commander
    }

    override fun killedNeutralMonster(monster: Monster) {
        if (monster.location.isAreaType(AreaType.SNOW, AreaType.LAKE)) {
            //no card when monster on ice or water
            return
        }
        super.killedNeutralMonster(monster)
    }


}

class FireRain :
    Card(76, "Feuerregen", "Verursacht 1 Schaden an einer beliebigen Einheit") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val unit = game.selectHostileUnit(owner, "Welcher Einheit soll Schaden zugeführt werden?")
        if (unit != null) {
            game.damage(unit)
        }
    }
}

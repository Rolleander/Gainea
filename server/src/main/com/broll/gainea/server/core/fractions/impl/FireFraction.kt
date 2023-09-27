package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.SelectionUtils

class FireFraction : Fraction(FractionType.FIRE) {
    private var turns = 0
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("Erhält jede dritte Runde eine Feuerregen-Karte\n(Verursacht 1 Schaden an einer beliebigen feindlichen Einheit)")
        desc.plus("Zahl +1 auf Wüsten")
        desc.contra("Erhält keine Belohnung für besiegte Monster auf Schnee oder Seen")
        return desc
    }

    override fun powerMutatorArea(power: FightingPower?, area: Area?) {
        if (area.getType() == AreaType.DESERT) {
            power!!.changeNumberPlus(1)
        }
    }

    override fun prepareTurn(actionHandlers: ActionHandlers?) {
        turns++
        if (turns == 3) {
            owner.cardHandler.receiveCard(FireRain())
            turns = 0
        }
        super.prepareTurn(actionHandlers)
    }

    override fun createSoldier(): Soldier {
        val soldier = Soldier(owner)
        soldier.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        soldier.name = "Feuermagier"
        soldier.icon = 23
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander = Soldier(owner)
        commander.isCommander = true
        commander.setStats(Fraction.Companion.COMMANDER_POWER, Fraction.Companion.COMMANDER_HEALTH)
        commander.name = "Flammenschürer Duras"
        commander.icon = 48
        return commander
    }

    override fun killedMonster(monster: Monster?) {
        if (LocationUtils.isAreaType(monster.getLocation(), AreaType.SNOW, AreaType.LAKE)) {
            //no card when monster on ice or water
            return
        }
        super.killedMonster(monster)
    }

    private inner class FireRain : Card(76, "Feuerregen", "Verursacht 1 Schaden an einer beliebigen feindlichen Einheit") {
        override val isPlayable: Boolean
            get() = true

        override fun play() {
            val unit = SelectionUtils.selectHostileUnit(game!!, owner!!, "Welcher Einheit soll Schaden zugeführt werden?")
            if (unit != null) {
                damage(game, unit, 1)
            }
        }
    }
}

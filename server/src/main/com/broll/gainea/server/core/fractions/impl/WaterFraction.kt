package com.broll.gainea.server.core.fractions.impl

import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionDescription
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.AreaType
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.buffs.BuffType
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.LocationUtils
import java.util.function.Consumer

class WaterFraction : Fraction(FractionType.WATER) {
    private val spawns: MutableList<Unit> = ArrayList()
    override fun description(): FractionDescription {
        val desc = FractionDescription("")
        desc.plus("""Fällt Arn wird er in eurem nächsten Zug als Eiskoloss (2/4) wiederbelebt
Der Eiskoloss kann für $FROZEN_ROUNDS Runden nicht angreifen oder sich bewegen
Fällt der Eiskoloss kehrt Arn in eruem nächsten Zug zurück""")
        desc.plus("Einheiten auf Seen können eine weitere Aktion durchführen")
        desc.contra("Auf Wüsten und Bergen -1 Zahl")
        return desc
    }

    override fun powerMutatorArea(power: FightingPower?, area: Area?) {
        if (area.getType() == AreaType.DESERT || area.getType() == AreaType.MOUNTAIN) {
            power!!.changeNumberPlus(-1)
        }
    }

    override fun turnStarted(actionHandlers: ActionHandlers?) {
        spawns.forEach(Consumer<Unit> { unit: Unit -> spawn(game, unit, unit.location) })
        spawns.clear()
    }

    override fun createSoldier(): Soldier {
        val soldier: Soldier = WaterSoldier(owner)
        soldier.setStats(Fraction.Companion.SOLDIER_POWER, Fraction.Companion.SOLDIER_HEALTH)
        soldier.name = "Wassermagier"
        soldier.icon = 46
        return soldier
    }

    override fun createCommander(): Soldier {
        val commander: Soldier = object : WaterSoldier(owner) {
            override fun onDeath(throughBattle: BattleResult?) {
                val summon: Unit = IceSummon()
                summon.owner = owner
                summon.location = location
                spawns.add(summon)
            }
        }
        commander.isCommander = true
        commander.setStats(Fraction.Companion.COMMANDER_POWER, Fraction.Companion.COMMANDER_HEALTH)
        commander.name = "Frostbeschwörer Arn"
        commander.icon = 116
        return commander
    }

    private open class WaterSoldier(owner: Player?) : Soldier(owner) {
        override fun moved() {
            super.moved()
            if (LocationUtils.isAreaType(location, AreaType.LAKE)) {
                turnStart()
            }
        }
    }

    private inner class IceSummon : Monster() {
        init {
            icon = 117
            name = "Eiskoloss"
            setStats(2, 4)
            val debuff = IntBuff(BuffType.SET, 0)
            getMovesPerTurn()!!.addBuff(debuff)
            attacksPerTurn.addBuff(debuff)
            this@WaterFraction.game.buffProcessor.timeoutBuff(debuff, FROZEN_ROUNDS)
        }

        override fun calcFightingPower(context: BattleContext?): FightingPower? {
            val power = super.calcFightingPower(context)
            if (context!!.location is Area) {
                powerMutatorArea(power, context.location as Area)
            }
            return power
        }

        override fun moved() {
            super.moved()
            if (LocationUtils.isAreaType(location, AreaType.LAKE)) {
                turnStart()
            }
        }

        override fun onDeath(throughBattle: BattleResult?) {
            val commander: Unit = createCommander()
            commander.location = location
            spawns.add(commander)
        }
    }

    companion object {
        private const val FROZEN_ROUNDS = 2
    }
}

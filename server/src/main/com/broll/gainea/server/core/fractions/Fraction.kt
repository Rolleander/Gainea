package com.broll.gainea.server.core.fractions

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.objects.IUnit
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.resolve
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.getActionHandler
import com.broll.gainea.server.core.utils.getSpawnLocations

abstract class Fraction(val type: FractionType) : GameUpdateReceiverAdapter() {
    abstract val description: FractionDescription
    protected lateinit var game: Game
    protected lateinit var owner: Player


    fun init(game: Game, owner: Player) {
        this.game = game
        this.owner = owner
    }

    open fun prepareTurn() {
        placeSoldier()
        prepareUnits()
    }

    fun placeSoldier() {
        val locations = game.getSpawnLocations(owner)
        if (locations.isNotEmpty()) {
            game.getActionHandler(PlaceUnitAction::class.java)
                .placeUnit(owner, createSoldier(), locations)
        }
    }

    protected fun prepareUnits() = owner.units.forEach { it.prepareForTurnStart() }

    open fun killedMonster(monster: Monster, battleResult: BattleResult) {
        owner.goalHandler.addStars(monster.stars)
        if (battleResult.getSnapshot(monster).owner.isNeutral()) {
            owner.cardHandler.drawRandomCard()
        }
    }

    open fun isHostile(unit: Unit): Boolean {
        return unit.owner !== owner
    }

    open fun createSoldier(): Soldier {
        val soldier = Soldier(owner, fraction = this)
        soldier.initFrom(description.soldier)
        return soldier
    }

    open fun createCommander(): Soldier {
        val commander = Soldier(owner, fraction = this)
        commander.commander = true
        commander.initFrom(description.commander)
        return commander
    }

    protected fun Soldier.initFrom(desc: UnitDescription) {
        setStats(desc.power, desc.health)
        name = desc.name
        icon = desc.icon
        description = desc.description
    }

    open fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = FightingPower(soldier)
        if (context.location is Area) {
            powerMutatorArea(power, context.location as Area)
        }
        return power
    }

    fun IUnit.isFromFraction() = resolve().run { this is Soldier && this.fraction == this@Fraction }

    protected open fun powerMutatorArea(power: FightingPower, area: Area) {}

    companion object {
        const val SOLDIER_HEALTH = 1
        const val SOLDIER_POWER = 1
        const val COMMANDER_HEALTH = 3
        const val COMMANDER_POWER = 3
    }
}

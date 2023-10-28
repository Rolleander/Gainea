package com.broll.gainea.server.core.fractions

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter
import com.broll.gainea.server.core.utils.getRandomFree

abstract class Fraction(val type: FractionType) : GameUpdateReceiverAdapter() {
    val description: FractionDescription
    protected lateinit var game: Game
    protected lateinit var owner: Player

    init {
        description = description()
    }

    protected abstract fun description(): FractionDescription
    fun init(game: Game, owner: Player) {
        this.game = game
        this.owner = owner
    }

    open fun prepareTurn(actionHandlers: ActionHandlers) {
        //default place one new soldier on an occupied location
        val spawnLocations = owner.controlledLocations.toMutableSet()
        if (spawnLocations.isEmpty()) {
            //player has no more controlled locations. give him a random free one
            val location = game.map.allAreas.getRandomFree()
                    ?: //no more free locations, just skip
                    return
            spawnLocations.add(location)
        }
        val placeUnitAction = actionHandlers.getHandler(PlaceUnitAction::class.java)
        placeUnitAction.placeSoldier(owner, spawnLocations.toList())
    }

    open fun turnStarted(actionHandlers: ActionHandlers) {}
    open fun killedMonster(monster: Monster) {
        owner.cardHandler.drawRandomCard()
    }

    open fun isHostile(unit: Unit): Boolean {
        return unit.owner !== owner
    }

    abstract fun createSoldier(): Soldier
    abstract fun createCommander(): Soldier
    open fun calcFightingPower(soldier: Soldier, context: BattleContext): FightingPower {
        val power = FightingPower(soldier)
        if (context.location is Area) {
            powerMutatorArea(power, context.location as Area)
        }
        return power
    }

    fun Unit.isFromFraction() = this is Soldier && this.fraction == this@Fraction

    protected open fun powerMutatorArea(power: FightingPower, area: Area) {}

    companion object {
        const val SOLDIER_HEALTH = 1
        const val SOLDIER_POWER = 1
        const val COMMANDER_HEALTH = 3
        const val COMMANDER_POWER = 3
    }
}

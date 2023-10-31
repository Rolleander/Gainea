package com.broll.gainea.server.core.battle

import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.IUnit
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral


open abstract class AbstractBattleContext<U : IUnit>(var attackers: List<U>, var defenders: List<U>) {
    var location: Location
        protected set
    var sourceLocation: Location
        protected set

    protected val attackingPlayers: List<Player>
    protected val defendingPlayers: List<Player>

    init {
        location = defenders[0].location
        sourceLocation = attackers[0].location
        attackingPlayers = attackers.map { it.owner }.distinct()
        defendingPlayers = defenders.map { it.owner }.distinct()
    }

    fun getLocation(player: Player): Location? {
        if (isAttacker(player)) {
            return sourceLocation
        } else if (isDefender(player)) {
            return location
        }
        return null
    }

    val aliveAttackers: List<U>
        get() = attackers.filter { it.alive }
    val aliveDefenders: List<U>
        get() = defenders.filter { it.alive }
    val killedAttackers: List<U>
        get() = attackers.filter { it.dead }
    val killedDefenders: List<U>
        get() = defenders.filter { it.dead }

    fun hasSurvivingAttackers() = aliveAttackers.isNotEmpty()

    fun hasSurvivingDefenders() = aliveDefenders.isNotEmpty()

    fun getFightingArmy(unit: U): List<U> {
        if (isAttacking(unit)) {
            return aliveAttackers
        } else if (isDefending(unit)) {
            return aliveDefenders
        }
        return listOf()
    }

    fun getUnits(player: Player): List<U> {
        if (isAttacker(player)) {
            return attackers.filter { it.owner == player }
        } else if (isDefender(player)) {
            return defenders.filter { it.owner == player }
        }
        return listOf()
    }

    fun getOpposingUnits(player: Player): List<U> {
        if (isAttacker(player)) {
            return defenders
        } else if (isDefender(player)) {
            return attackers
        }
        return listOf()
    }

    fun getOpposingFightingArmy(unit: U): List<U> {
        if (isAttacking(unit)) {
            return aliveDefenders
        } else if (isDefending(unit)) {
            return aliveAttackers
        }
        return listOf()
    }

    fun isParticipating(player: Player) = isAttacker(player) || isDefender(player)


    fun isAttacker(player: Player) = attackingPlayers.contains(player)


    fun isAttacking(unit: U) = attackers.any { it.id == unit.id }


    fun isDefending(unit: U) = defenders.any { it.id == unit.id }


    fun isDefender(player: Player) = defendingPlayers.contains(player)


    val isNeutralAttacker: Boolean
        get() = isNeutralOwner(attackingPlayers)
    val isNeutralDefender: Boolean
        get() = isNeutralOwner(defendingPlayers)
    val isNeutralParticipant: Boolean
        get() = isNeutralAttacker || isNeutralDefender

    protected fun isNeutralOwner(players: List<Player>): Boolean {
        return players.size == 1 && players[0].isNeutral()
    }

    protected fun getNonNeutralOwners(owners: List<Player>) = owners.filterNot { it.isNeutral() }

    fun getNonNeutralAttackers() =
            getNonNeutralOwners(attackingPlayers)

    fun getNonNeutralDefenders() =
            getNonNeutralOwners(defendingPlayers)

    fun getControllingAttacker() = getNonNeutralAttackers().firstOrNull()

}

class BattleContext(attackers: List<Unit>, defenders: List<Unit>) : AbstractBattleContext<Unit>(attackers, defenders)
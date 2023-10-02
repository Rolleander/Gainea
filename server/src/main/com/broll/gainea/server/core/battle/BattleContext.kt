package com.broll.gainea.server.core.battle

import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.owner

open class BattleContext(var attackers: List<Unit>, var defenders: List<Unit>) {
    var location: Location
        protected set
    var sourceLocation: Location
        protected set
    var attackingPlayer: Player
        protected set
    protected var defendingPlayers: List<Player>

    init {
        location = defenders[0].location
        sourceLocation = attackers[0].location
        attackingPlayer = attackers.owner()
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

    val aliveAttackers: List<Unit>
        get() = attackers.filter { it.alive }
    val aliveDefenders: List<Unit>
        get() = defenders.filter { it.alive }
    val killedAttackers: List<Unit>
        get() = attackers.filter { it.dead }
    val killedDefenders: List<Unit>
        get() = defenders.filter { it.dead }

    fun hasSurvivingAttackers() = aliveAttackers.isNotEmpty()

    fun hasSurvivingDefenders() = aliveDefenders.isNotEmpty()

    fun getFightingArmy(unit: Unit): List<Unit> {
        if (isAttacking(unit)) {
            return aliveAttackers
        } else if (isDefending(unit)) {
            return aliveDefenders
        }
        return listOf()
    }

    fun getUnits(player: Player): List<Unit> {
        if (isAttacker(player)) {
            return attackers.filter { it.owner == player }
        } else if (isDefender(player)) {
            return defenders.filter { it.owner == player }
        }
        return listOf()
    }

    fun getOpposingUnits(player: Player): List<Unit> {
        if (isAttacker(player)) {
            return defenders
        } else if (isDefender(player)) {
            return attackers
        }
        return listOf()
    }

    fun getOpposingFightingArmy(unit: Unit): List<Unit> {
        if (isAttacking(unit)) {
            return aliveDefenders
        } else if (isDefending(unit)) {
            return aliveAttackers
        }
        return listOf()
    }

    fun isParticipating(player: Player) = isAttacker(player) || isDefender(player)


    fun isAttacker(player: Player) = attackingPlayer === player


    fun isAttacking(unit: Unit) = attackers.contains(unit)


    fun isDefending(unit: Unit) = defenders.contains(unit)


    fun isDefender(player: Player) = defendingPlayers.contains(player)


    val isNeutralAttacker: Boolean
        get() = attackingPlayer.isNeutral()
    val isNeutralDefender: Boolean
        get() = isNeutralOwner(defendingPlayers)
    val isNeutralParticipant: Boolean
        get() = isNeutralAttacker || isNeutralDefender

    protected fun isNeutralOwner(players: List<Player>): Boolean {
        return players.size == 1 && players[0].isNeutral()
    }

    protected fun getNonNeutralOwners(owners: List<Player>) = owners.filterNot { it.isNeutral() }

    fun getNonNeutralDefenders() =
            getNonNeutralOwners(defendingPlayers)

}

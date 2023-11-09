package com.broll.gainea.server.core.battle

import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.IUnit
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral


abstract class AbstractBattleContext<U : IUnit>(
    attackers: List<U>,
    defenders: List<U>,
    val rounds: MutableList<BattleRound> = mutableListOf()
) {

    var attackers: List<U> = attackers
        protected set
    var defenders: List<U> = defenders
        protected set

    val location: Location
        get() = defenders[0].location
    val sourceLocation: Location
        get() = attackers[0].location

    val attackingPlayers: List<Player>
        get() = attackers.map { it.owner }.distinct()
    val defendingPlayers: List<Player>
        get() = defenders.map { it.owner }.distinct()


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

    fun getFightingArmy(unit: IUnit): List<U> {
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

    fun getOpposingFightingArmy(unit: IUnit): List<U> {
        if (isAttacking(unit)) {
            return aliveDefenders
        } else if (isDefending(unit)) {
            return aliveAttackers
        }
        return listOf()
    }

    fun isParticipating(player: Player) = isAttacker(player) || isDefender(player)

    fun isParticipating(unit: IUnit) = attackers.contains(unit) || defenders.contains(unit)

    fun isAttacker(player: Player) = attackingPlayers.contains(player)


    fun isAttacking(unit: IUnit) = attackers.contains(unit)


    fun isDefending(unit: IUnit) = defenders.contains(unit)


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

data class BattleRound(val damageTaken: List<FightResult.AttackDamage>)

class BattleContext(attackers: List<Unit>, defenders: List<Unit>) :
    AbstractBattleContext<Unit>(attackers, defenders) {

    fun addAttacker(unit: Unit) {
        this.attackers = this.attackers.toMutableList().also { it.add(unit) }
    }

    fun addDefender(unit: Unit) {
        this.defenders = this.defenders.toMutableList().also { it.add(unit) }
    }

}
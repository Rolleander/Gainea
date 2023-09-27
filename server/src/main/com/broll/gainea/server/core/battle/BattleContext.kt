package com.broll.gainea.server.core.battle

import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.PlayerUtils
import java.util.Objects
import java.util.stream.Collectors

open class BattleContext(var attackers: List<Unit?>?, var defenders: List<Unit?>?) {
    var location: Location?
        protected set
    var sourceLocation: Location?
        protected set
    var attackingPlayer: Player?
        protected set
    protected var defendingPlayers: List<Player?>

    init {
        location = defenders!![0].getLocation()
        sourceLocation = attackers!![0].getLocation()
        attackingPlayer = PlayerUtils.getOwner(attackers)
        defendingPlayers = defenders!!.stream().map { obj: Unit? -> obj.getOwner() }.distinct().collect(Collectors.toList())
    }

    fun getLocation(player: Player?): Location? {
        if (isAttacker(player)) {
            return sourceLocation
        } else if (isDefender(player)) {
            return location
        }
        return null
    }

    val aliveAttackers: List<Unit?>
        get() = attackers!!.stream().filter { obj: Unit? -> obj!!.isAlive }.collect(Collectors.toList())
    val aliveDefenders: List<Unit?>
        get() = defenders!!.stream().filter { obj: Unit? -> obj!!.isAlive }.collect(Collectors.toList())
    val killedAttackers: List<Unit?>
        get() = attackers!!.stream().filter { obj: Unit? -> obj!!.isDead }.collect(Collectors.toList())
    val killedDefenders: List<Unit?>
        get() = defenders!!.stream().filter { obj: Unit? -> obj!!.isDead }.collect(Collectors.toList())

    fun hasSurvivingAttackers(): Boolean {
        return !aliveAttackers.isEmpty()
    }

    fun hasSurvivingDefenders(): Boolean {
        return !aliveDefenders.isEmpty()
    }

    fun getFightingArmy(unit: Unit?): List<Unit?> {
        if (isAttacking(unit)) {
            return aliveAttackers
        } else if (isDefending(unit)) {
            return aliveDefenders
        }
        return ArrayList()
    }

    fun getUnits(player: Player): List<Unit?> {
        if (isAttacker(player)) {
            return attackers!!.stream().filter { it: Unit? -> it.getOwner() === player }.collect(Collectors.toList())
        } else if (isDefender(player)) {
            return defenders!!.stream().filter { it: Unit? -> it.getOwner() === player }.collect(Collectors.toList())
        }
        return ArrayList()
    }

    fun getOpposingUnits(player: Player?): List<Unit?>? {
        if (isAttacker(player)) {
            return defenders
        } else if (isDefender(player)) {
            return attackers
        }
        return ArrayList()
    }

    fun getOpposingFightingArmy(unit: Unit?): List<Unit?> {
        if (isAttacking(unit)) {
            return aliveDefenders
        } else if (isDefending(unit)) {
            return aliveAttackers
        }
        return ArrayList()
    }

    fun isParticipating(player: Player?): Boolean {
        return isAttacker(player) || isDefender(player)
    }

    fun isAttacker(player: Player?): Boolean {
        return attackingPlayer === player
    }

    fun isAttacking(unit: Unit?): Boolean {
        return attackers!!.contains(unit)
    }

    fun isDefending(unit: Unit?): Boolean {
        return defenders!!.contains(unit)
    }

    fun isDefender(player: Player?): Boolean {
        return defendingPlayers.contains(player)
    }

    val isNeutralAttacker: Boolean
        get() = attackingPlayer == null
    val isNeutralDefender: Boolean
        get() = isNeutralOwner(defendingPlayers)
    val isNeutralParticipant: Boolean
        get() = isNeutralAttacker || isNeutralDefender

    protected fun isNeutralOwner(players: List<Player?>): Boolean {
        return players.size == 1 && players[0] == null
    }

    protected fun getNonNeutralOwners(owners: List<Player?>): List<Player?> {
        return owners.stream().filter { obj: Player? -> Objects.nonNull(obj) }.collect(Collectors.toList())
    }

    fun getDefendingPlayers(): List<Player?> {
        return getNonNeutralOwners(defendingPlayers)
    }
}

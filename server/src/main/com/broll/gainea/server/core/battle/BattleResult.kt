package com.broll.gainea.server.core.battle

import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.google.common.collect.Lists
import java.util.Objects
import java.util.stream.Collectors

class BattleResult(private val retreated: Boolean, context: BattleContext?) : BattleContext(context!!.attackers, context.defenders) {
    private val attackersWon: Boolean
    private val defendersWon: Boolean

    init {
        attackersWon = !retreated && hasSurvivingAttackers() && !hasSurvivingDefenders()
        defendersWon = retreated || hasSurvivingDefenders() && !hasSurvivingAttackers()
    }

    val winningPlayers: List<Player?>?
        get() = getNonNeutralOwners(winnerOwners)
    val losingPlayers: List<Player?>?
        get() = getNonNeutralOwners(loserOwners)

    fun isWinner(player: Player?): Boolean {
        return winnerOwners.contains(player)
    }

    fun isLoser(player: Player?): Boolean {
        return loserOwners.contains(player)
    }

    val isNeutralWinner: Boolean
        get() = isNeutralOwner(winnerOwners)
    val isNeutralLoser: Boolean
        get() = isNeutralOwner(loserOwners)
    val attackerEndLocation: Location?
        get() = if (attackersWon()) getLocation() else getSourceLocation()

    fun attackersWon(): Boolean {
        return attackersWon
    }

    fun defendersWon(): Boolean {
        return defendersWon
    }

    private val winnerOwners: List<Player?>
        private get() {
            if (attackersWon()) {
                return Lists.newArrayList(attackingPlayer)
            } else if (defendersWon()) {
                return defendingPlayers
            }
            return ArrayList()
        }
    private val loserOwners: List<Player?>
        private get() {
            if (attackersWon()) {
                return defendingPlayers
            } else if (defendersWon()) {
                return Lists.newArrayList(attackingPlayer)
            }
            return ArrayList()
        }

    fun attackerRetreated(): Boolean {
        return retreated
    }

    val isDraw: Boolean
        get() = !attackersWon() && !defendersWon()
    val winnerUnits: List<Unit?>?
        get() {
            if (attackersWon()) {
                return getAttackers()
            } else if (defendersWon()) {
                return getDefenders()
            }
            return ArrayList()
        }
    val loserUnits: List<Unit?>?
        get() {
            if (attackersWon()) {
                return getDefenders()
            } else if (defendersWon()) {
                return getAttackers()
            }
            return ArrayList()
        }
    val winnerSurvivingUnits: List<Unit?>?
        get() {
            if (attackersWon()) {
                return aliveAttackers
            } else if (defendersWon()) {
                return aliveDefenders
            }
            return ArrayList()
        }
    val loserSurvivingUnits: List<Unit?>?
        get() {
            if (attackersWon()) {
                return aliveDefenders
            } else if (defendersWon()) {
                return aliveAttackers
            }
            return ArrayList()
        }
    val winnerDeadUnits: List<Unit?>?
        get() {
            if (attackersWon()) {
                return killedAttackers
            } else if (defendersWon()) {
                return killedDefenders
            }
            return ArrayList()
        }
    val loserDeadUnits: List<Unit?>?
        get() {
            if (attackersWon()) {
                return killedDefenders
            } else if (defendersWon()) {
                return killedAttackers
            }
            return ArrayList()
        }

    fun getEndLocation(player: Player?): Location? {
        if (isAttacker(player)) {
            return attackerEndLocation
        } else if (isDefender(player)) {
            return location
        }
        return null
    }

    fun getKillingPlayers(unit: Unit?): List<Player?>? {
        if (isAttacking(unit)) {
            return getDefendingPlayers()
        } else if (isDefending(unit)) {
            return Lists.newArrayList(getAttackingPlayer()).stream().filter { obj: Player? -> Objects.nonNull(obj) }.collect(Collectors.toList())
        }
        return ArrayList()
    }
}

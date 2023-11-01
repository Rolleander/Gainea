package com.broll.gainea.server.core.battle

import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.IUnit
import com.broll.gainea.server.core.player.Player

class BattleResult(val retreated: Boolean, context: BattleContext) :
    AbstractBattleContext<UnitSnapshot>(
        context.attackers.map { UnitSnapshot(it) },
        context.defenders.map { UnitSnapshot(it) },
        context.rounds
    ) {
    val attackersWon: Boolean = !retreated && hasSurvivingAttackers() && !hasSurvivingDefenders()
    val defendersWon: Boolean = retreated || hasSurvivingDefenders() && !hasSurvivingAttackers()

    val winningPlayers: List<Player>
        get() = getNonNeutralOwners(winnerOwners)
    val losingPlayers: List<Player>
        get() = getNonNeutralOwners(loserOwners)

    fun isWinner(player: Player) = winnerOwners.contains(player)


    fun isLoser(player: Player) = loserOwners.contains(player)

    val isNeutralWinner: Boolean
        get() = isNeutralOwner(winnerOwners)
    val isNeutralLoser: Boolean
        get() = isNeutralOwner(loserOwners)
    val attackerEndLocation: Location
        get() = if (attackersWon) location else sourceLocation

    private val winnerOwners: List<Player>
        get() {
            if (attackersWon) {
                return attackingPlayers
            } else if (defendersWon) {
                return defendingPlayers
            }
            return listOf()
        }
    private val loserOwners: List<Player>
        get() {
            if (attackersWon) {
                return defendingPlayers
            } else if (defendersWon) {
                return attackingPlayers
            }
            return listOf()
        }

    val isDraw: Boolean
        get() = !attackersWon && !defendersWon
    val winnerUnits: List<UnitSnapshot>
        get() {
            if (attackersWon) {
                return attackers
            } else if (defendersWon) {
                return defenders
            }
            return listOf()
        }
    val loserUnits: List<UnitSnapshot>
        get() {
            if (attackersWon) {
                return defenders
            } else if (defendersWon) {
                return attackers
            }
            return listOf()
        }
    val winnerSurvivingUnits: List<UnitSnapshot>
        get() {
            if (attackersWon) {
                return aliveAttackers
            } else if (defendersWon) {
                return aliveDefenders
            }
            return listOf()
        }
    val loserSurvivingUnits: List<UnitSnapshot>
        get() {
            if (attackersWon) {
                return aliveDefenders
            } else if (defendersWon) {
                return aliveAttackers
            }
            return listOf()
        }
    val winnerDeadUnits: List<UnitSnapshot>
        get() {
            if (attackersWon) {
                return killedAttackers
            } else if (defendersWon) {
                return killedDefenders
            }
            return listOf()
        }
    val loserDeadUnits: List<UnitSnapshot>
        get() {
            if (attackersWon) {
                return killedDefenders
            } else if (defendersWon) {
                return killedAttackers
            }
            return listOf()
        }

    fun getDamageDealt(player: Player) =
        rounds.flatMap { it.damageTaken }.count { it.source.owner == player }

    fun getEndLocation(player: Player): Location? {
        if (isAttacker(player)) {
            return attackerEndLocation
        } else if (isDefender(player)) {
            return location
        }
        return null
    }

    fun getKillingPlayers(unit: IUnit): List<Player> {
        if (killedAttackers.any { it.id == unit.id }) {
            return defendingPlayers
        } else if (killedDefenders.any { it.id == unit.id }) {
            return attackingPlayers
        }
        return listOf()
    }
}

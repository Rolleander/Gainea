package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.core.objects.monster.MonsterBehavior
import com.broll.gainea.net.proto.NT_Monster
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.MonsterActivity.SOMETIMES
import com.broll.gainea.core.objects.monster.MonsterBehavior.RESIDENT
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral

open class Monster(owner: Player) : Unit(owner) {
    var behavior = RESIDENT
        set(value) {
            field = value
            if (value == RESIDENT) {
                removeActionTimer()
            } else if (actionTimer == NO_ACTION_TIMER) {
                actionTimer = activity.turnTimer
            }
        }
    var activity: MonsterActivity = SOMETIMES
        set(value) {
            field = value
            resetActionTimer()
        }
    var motion = MonsterMotion.TERRESTRIAL
    private var actionTimer = NO_ACTION_TIMER
    fun removeActionTimer() {
        actionTimer = NO_ACTION_TIMER
    }

    val stars: Int
        get() = getStars(this)

    private fun resetActionTimer() {
        if (behavior != RESIDENT) {
            actionTimer = activity.turnTimer
        }
    }

    fun monsterNt(): NT_Monster {
        return fillBattleObject(NT_Monster.newBuilder())
            .setStars(stars)
            .setActionTimer(actionTimer)
            .setBehavior(behavior.ordinal)
            .build()
    }

    override fun nt() = com.broll.gainea.net.proto.NT_Unit.newBuilder()
        .setMonster(monsterNt()).build()

    override fun boardNt() = com.broll.gainea.net.proto.NT_BoardObject.newBuilder()
        .setMonster(monsterNt()).build()

    override fun roundStarted() {
        if (actionTimer != NO_ACTION_TIMER && alive && isBehaviorActive && game.rounds > 1) {
            progressBehavior()
        }
    }

    override fun canMoveTo(to: Location): Boolean {
        val canMove = motion.canMoveTo(currentLocation = location, newLocation = to)
        //allow controlled monsters to walk with the players army
        if (!owner.isNeutral()) {
            return canMove || super.canMoveTo(to)
        }
        return canMove
    }

    fun mightAttackSoon() =
        actionTimer == 1 && behavior != RESIDENT && behavior != MonsterBehavior.FRIENDLY


    private val isBehaviorActive: Boolean
        get() = if (owner.isNeutral()) {
            true
        } else !controllable

    private fun progressBehavior() {
        actionTimer--
        if (actionTimer == 0) {
            behavior.doAction(game, this)
            resetActionTimer()
        }
    }

    companion object {
        private const val MAX_STARS = 8
        private const val NO_ACTION_TIMER = -1
        fun getStars(monster: Monster): Int {
            return stars(monster.power.rootValue, monster.maxHealth.rootValue)
        }

        fun stars(power: Int, health: Int): Int {
            return Math.min((power + health) / 2, MAX_STARS)
        }
    }
}

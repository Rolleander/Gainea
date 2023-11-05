package com.broll.gainea.server.core.objects.monster

import com.broll.gainea.net.NT_Monster
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral

open class Monster(owner: Player) : Unit(owner) {
    var behavior = MonsterBehavior.RESIDENT
    var activity: MonsterActivity = MonsterActivity.SOMETIMES
        set(value) {
            field = value
            resetActionTimer()
        }
    var motion = MonsterMotion.TERRESTRIAL
    private var actionTimer = NT_Monster.NO_ACTION_TIMER.toInt()
    fun removeActionTimer() {
        actionTimer = NT_Monster.NO_ACTION_TIMER.toInt()
    }

    val stars: Int
        get() = getStars(this)

    private fun resetActionTimer() {
        if (behavior != MonsterBehavior.RESIDENT) {
            actionTimer = activity.turnTimer
        }
    }

    override fun nt(): NT_Monster {
        val monster = NT_Monster()
        monster.stars = stars.toByte()
        monster.actionTimer = actionTimer.toByte()
        monster.behavior = behavior.ordinal.toByte()
        fillBattleObject(monster)
        return monster
    }

    override fun roundStarted() {
        if (actionTimer != NT_Monster.NO_ACTION_TIMER.toInt() && alive && isBehaviorActive && game.rounds > 1) {
            progressBehavior()
        }
    }

    override fun canMoveTo(to: Location): Boolean {
        val canMove = motion.canMoveTo(currentLocation = location, newLocation = to)
        //allow controlled monsters to walk with the players army
        return if (!canMove && !owner.isNeutral()) super.canMoveTo(to) else canMove
    }

    fun mightAttackSoon() =
        actionTimer == 1 && behavior != MonsterBehavior.RESIDENT && behavior != MonsterBehavior.FRIENDLY


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
        fun getStars(monster: Monster): Int {
            return stars(monster.power.rootValue, monster.maxHealth.rootValue)
        }

        fun stars(power: Int, health: Int): Int {
            return Math.min((power + health) / 2, MAX_STARS)
        }
    }
}

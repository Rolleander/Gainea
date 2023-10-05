package com.broll.gainea.server.core.objects

import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.battle.BattleContext
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.battle.FightingPower
import com.broll.gainea.server.core.objects.buffs.BuffableInt
import com.broll.gainea.server.core.objects.buffs.IntBuff
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral

abstract class Unit(owner: Player) : MapObject(owner) {
    var maxHealth: BuffableInt<MapObject> = BuffableInt(this, 0)

    var power: BuffableInt<MapObject> = BuffableInt(this, 0)

    var health: BuffableInt<MapObject> = BuffableInt(this, 0)

    var attacksPerTurn: BuffableInt<MapObject> = BuffableInt(this, 1) //default 1 attack

    var numberPlus: BuffableInt<MapObject> = BuffableInt(this, 0)

    private var attackCount = 0
    var isMoveOrAttackRestriction = true //usually units can only attack or move in one turn
    private var attacked = false
    private var moved = false
    var controllable = true
        protected set
    private var type = NT_Unit.TYPE_MALE.toInt()
    var kills = 0
        private set
    private var justSpawned = true

    init {
        health.setMinValue(0)
        maxHealth.setMinValue(1)
        power.setMinValue(0)
    }

    override fun turnStart() {
        super.turnStart()
        justSpawned = false
        attackCount = 0
        moved = false
        attacked = false
    }

    override fun hasRemainingMove(): Boolean {
        if (justSpawned) return false
        return if (isMoveOrAttackRestriction && attacked) {
            false
        } else super.hasRemainingMove()
    }

    fun hasRemainingAttack(): Boolean {
        if (justSpawned) return false
        return if (isMoveOrAttackRestriction && moved) {
            false
        } else attackCount < attacksPerTurn!!.value!!
    }

    open fun onDeath(throughBattle: BattleResult?) {}
    open fun calcFightingPower(context: BattleContext) =
            FightingPower(this).changeNumberPlus(numberPlus.value)


    override fun moved() {
        super.moved()
        moved = true
    }

    fun attacked() {
        attackCount++
        attacked = true
    }

    fun setStats(power: Int, health: Int) {
        setPower(power)
        setHealth(health)
    }


    fun takeDamage(damage: Int = 1): Boolean {
        val aliveBefore = alive
        health.addValue(-damage)
        return aliveBefore && dead
    }

    fun heal(heal: Int) {
        health.addValue(heal)
        if (health.value > maxHealth.value) {
            heal()
        }
    }

    val dead: Boolean
        get() = health.value <= 0
    val alive: Boolean
        get() = !dead

    fun heal() {
        health.value = maxHealth.rootValue
    }

    fun setHealth(health: Int) {
        this.health.value = health
        maxHealth.value = health
    }

    fun addHealth(change: Int) {
        health.addValue(change)
        maxHealth.addValue(change)
    }

    fun overwriteHealth(health: BuffableInt<MapObject>) {
        this.health = health
    }

    fun setPower(power: Int) {
        this.power.value = power
    }

    fun addHealthBuff(buff: IntBuff) {
        health.addBuff(buff)
        maxHealth.addBuff(buff)
    }

    fun clearBuffs() {
        power.clearBuffs()
        health.clearBuffs()
        maxHealth.clearBuffs()
        attacksPerTurn.clearBuffs()
        movesPerTurn.clearBuffs()
    }

    val battleStrength: Int
        get() = power.value + health.value

    override fun nt(): NT_Unit {
        val unit = NT_Unit()
        fillBattleObject(unit)
        return unit
    }

    protected fun fillBattleObject(unit: NT_Unit) {
        fillObject(unit)
        unit.health = health.value.toShort()
        unit.maxHealth = maxHealth.value.toShort()
        unit.power = power.value.toShort()
        unit.type = type.toByte()
        unit.kills = kills.toShort()
        if (!owner.isNeutral()) {
            unit.owner = owner.serverPlayer.id.toShort()
        }
    }

    val hurt: Boolean
        get() = health.value < maxHealth.value

    fun setType(type: Int) {
        this.type = type
    }

    fun addKill() {
        kills++
    }

    override fun toString(): String {
        return "Unit{" +
                "id=" + id +
                ", name='" + name +
                ", " + power + "/" + health +
                ", location=" + location + '\'' +
                '}'
    }

    companion object {
        fun copy(from: Unit, to: Unit) {
            to.maxHealth = from.maxHealth.copy(to)
            to.power = from.power.copy(to)
            to.health = from.health.copy(to)
            to.owner = from.owner
            to.attackCount = from.attackCount
            to.moveCount = from.moveCount
            to.attacksPerTurn = from.attacksPerTurn.copy(to)
            to.movesPerTurn = from.movesPerTurn.copy(to)
            to.numberPlus = from.numberPlus.copy(to)
            to.isMoveOrAttackRestriction = from.isMoveOrAttackRestriction
            to.icon = from.icon
            to.location = from.location
            to.name = from.name
            to.scale = from.scale
        }
    }
}

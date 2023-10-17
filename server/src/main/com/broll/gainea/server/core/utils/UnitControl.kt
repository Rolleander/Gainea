package com.broll.gainea.server.core.utils

import com.broll.gainea.net.NT_Event
import com.broll.gainea.net.NT_Event_FocusObject
import com.broll.gainea.net.NT_Event_FocusObjects
import com.broll.gainea.net.NT_Event_MovedObject
import com.broll.gainea.net.NT_Event_PlacedObject
import com.broll.gainea.net.NT_Event_UpdateObjects
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.map.Area
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.google.common.collect.Lists
import org.apache.commons.collections4.ListUtils
import org.slf4j.LoggerFactory
import java.util.function.Consumer

object UnitControl {
    private val Log = LoggerFactory.getLogger(UnitControl::class.java)
    private const val MOVE_PAUSE = 1500
    private const val SPAWN_PAUSE = 1500
    private const val DAMAGE_PAUSE = 1000


    fun Unit.isNeutralMonster() = this is Monster && this.owner.isNeutral()


    fun Game.move(unit: MapObject, location: Location) =
            move(Lists.newArrayList(unit), location)


    fun Game.move(units: List<MapObject>, location: Location) {
        if (units.isEmpty()) return
        Log.trace("UnitControl: move units [" + units.size + "] to " + location)
        units.forEach { it.place(location) }
        val movedObject = NT_Event_MovedObject()
        movedObject.objects = units.map { it.nt() }.toTypedArray()
        sendUpdate(movedObject)
        updateReceiver.moved(units, location)
        ProcessingUtils.pause(MOVE_PAUSE)
    }

    fun Game.kill(unit: Unit) = damage(unit, Int.MAX_VALUE)


    fun Game.focus(obj: MapObject, effect: Int) {
        val nt = NT_Event_FocusObject()
        nt.screenEffect = effect
        nt.`object` = obj.nt()
        sendUpdate(nt)
        ProcessingUtils.pause(DAMAGE_PAUSE)
    }

    fun Game.focus(objects: List<MapObject>, effect: Int) {
        objects.map { it.location }.distinct().forEach { location ->
            val nt = NT_Event_FocusObjects()
            nt.screenEffect = effect
            nt.objects = objects.filter { it.location === location }.map { it.nt() }.toTypedArray()
            sendUpdate(nt)
            ProcessingUtils.pause(DAMAGE_PAUSE)
        }
    }

    fun Game.update(objects: List<MapObject>) {
        val nt = NT_Event_UpdateObjects()
        nt.objects = objects.map { it.nt() }.toTypedArray()
        sendUpdate(nt)
    }


    fun Game.heal(unit: Unit, heal: Int = 1, consumer: Consumer<NT_Event_FocusObject>? = null) {
        Log.trace("UnitControl: heal unit $unit for $heal")
        val actualHeal = Math.max(unit.maxHealth.value - unit.health.value, heal)
        unit.heal(actualHeal)
        val nt = NT_Event_FocusObject()
        nt.`object` = unit.nt()
        nt.screenEffect = NT_Event.EFFECT_HEAL
        consumer?.accept(nt)
        sendUpdate(nt)
        ProcessingUtils.pause(DAMAGE_PAUSE)
    }

    fun Game.damage(unit: Unit, damage: Int = 1, consumer: Consumer<NT_Event_FocusObject>? = null) {
        Log.trace("UnitControl: damage unit $unit for $damage")
        //dont overkill
        val actualDamage = Math.min(unit.health.value, damage)
        unit.takeDamage(actualDamage)
        val lethal = unit.dead
        if (lethal) {
            //remove unit
            val removed = remove(unit)
            if (!removed) {
                //was already killed/removed, do nothing
                return
            }
            Log.trace("Damaged unit died and is removed")
        }
        //send update to focus clients on object
        val nt = NT_Event_FocusObject()
        nt.`object` = unit.nt()
        nt.screenEffect = NT_Event.EFFECT_DAMAGE
        consumer?.accept(nt)
        sendUpdate(nt)
        updateReceiver.damaged(unit, damage)
        if (lethal) {
            unit.onDeath(null)
            updateReceiver.killed(unit, null)
        }
        ProcessingUtils.pause(DAMAGE_PAUSE)
    }

    fun Game.spawn(obj: MapObject, location: Location, consumer: Consumer<NT_Event_PlacedObject>? = null) {
        Log.trace("UnitControl: spawn object $obj at $location")
        obj.init(this)
        if (obj.owner.isNeutral()) {
            objects += obj
        } else if (obj is Unit) {
            obj.owner.units += obj
        }
        if (obj is Unit) {
            buffProcessor.applyGlobalBuffs(obj)
        }
        obj.place(location)
        updateReceiver.register(obj)
        val nt = NT_Event_PlacedObject()
        nt.`object` = obj.nt()
        nt.sound = obj.defaultSpawnSound(location)
        consumer?.accept(nt)
        sendUpdate(nt)
        updateReceiver.spawned(obj, location)
        ProcessingUtils.pause(SPAWN_PAUSE)
    }

    private fun MapObject.defaultSpawnSound(location: Location) =
            if (this is Monster) {
                "monster.ogg"
            } else {
                "recruit.ogg"
            }

    fun Game.spawnMonsters(count: Int) {
        map.allAreas.getRandomFree(count).forEach {
            spawnMonster(it as Area)
        }
    }

    fun Game.spawnMonster(area: Area) {
        val activeMonsters = objects.filterIsInstance<Monster>()
        monsterFactory.spawn(neutralPlayer, area.type, activeMonsters)?.let {
            Log.debug("spawn monster " + it.name + " on " + area)
            spawn(it, area)
        }
    }

    fun Game.conquer(units: List<Unit>, target: Location) {
        val targetUnits = units.owner().getHostileArmy(target)
        if (targetUnits.isEmpty()) {
            move(units, target)
        } else {
            battleHandler.startBattle(units, targetUnits)
        }
    }

    fun Game.recruit(newOwner: Player, units: List<Unit>, newLocation: Location? = null) {
        val recruit = units.filter { it.owner !== newOwner }
        if (recruit.isEmpty()) {
            return
        }
        val moveUnits = recruit.filter { it.alive && newLocation != null && it.location !== newLocation }
        val updateUnits = ListUtils.subtract(recruit, moveUnits)
        recruit.forEach {
            val previousOwner = it.owner
            if (previousOwner.isNeutral()) {
                objects.remove(it)
            } else {
                previousOwner.units.remove(it)
            }
            newOwner.units.add(it)
            it.owner = newOwner
            if (it.dead) {
                it.heal()
            }
            if (it is Monster) {
                it.removeActionTimer()
            }
        }
        if (moveUnits.isNotEmpty()) {
            move(moveUnits, newLocation!!)
        }
        if (updateUnits.isNotEmpty()) {
            if (newLocation != null) {
                updateUnits.forEach { it.location = newLocation }
            }
            update(updateUnits)
        }
    }

}
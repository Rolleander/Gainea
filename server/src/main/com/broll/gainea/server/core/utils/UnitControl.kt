package com.broll.gainea.server.core.utils

import com.broll.gainea.net.NT_Event
import com.broll.gainea.net.NT_Event_FocusObject
import com.broll.gainea.net.NT_Event_FocusObjects
import com.broll.gainea.net.NT_Event_MovedObject
import com.broll.gainea.net.NT_Event_PlacedObject
import com.broll.gainea.net.NT_Event_UpdateObjects
import com.broll.gainea.server.core.GameContainer
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

fun Unit.isNeutralMonster() = this is Monster && this.owner.isNeutral()

object UnitControl {
    private val Log = LoggerFactory.getLogger(UnitControl::class.java)
    private const val MOVE_PAUSE = 1500
    private const val SPAWN_PAUSE = 1500
    private const val DAMAGE_PAUSE = 1000
    fun move(game: GameContainer, unit: MapObject, location: Location) =
            move(game, Lists.newArrayList(unit), location)


    fun move(game: GameContainer, units: List<MapObject>, location: Location) {
        if (units.isEmpty()) return
        Log.trace("UnitControl: move units [" + units.size + "] to " + location)
        units.forEach { GameUtils.place(it, location) }
        val movedObject = NT_Event_MovedObject()
        movedObject.objects = units.map { it.nt() }.toTypedArray()
        GameUtils.sendUpdate(game, movedObject)
        game.updateReceiver.moved(units, location)
        ProcessingUtils.pause(MOVE_PAUSE)
    }

    fun kill(game: GameContainer, unit: Unit) =
            damage(game, unit, Int.MAX_VALUE)


    fun focus(game: GameContainer, obj: MapObject, effect: Int) {
        val nt = NT_Event_FocusObject()
        nt.screenEffect = effect
        nt.`object` = obj.nt()
        GameUtils.sendUpdate(game, nt)
        ProcessingUtils.pause(DAMAGE_PAUSE)
    }

    fun focus(game: GameContainer, objects: List<MapObject>, effect: Int) {
        objects.map { it.location }.distinct().forEach { location ->
            val nt = NT_Event_FocusObjects()
            nt.screenEffect = effect
            nt.objects = objects.filter { it.location === location }.map { it.nt() }.toTypedArray()
            GameUtils.sendUpdate(game, nt)
            ProcessingUtils.pause(DAMAGE_PAUSE)
        }
    }

    fun update(game: GameContainer, objects: List<MapObject>) {
        val nt = NT_Event_UpdateObjects()
        nt.objects = objects.map { it.nt() }.toTypedArray()
        GameUtils.sendUpdate(game, nt)
    }


    fun heal(game: GameContainer, unit: Unit, heal: Int = 1, consumer: Consumer<NT_Event_FocusObject>? = null) {
        Log.trace("UnitControl: heal unit $unit for $heal")
        val actualHeal = Math.max(unit.maxHealth.value - unit.health.value, heal)
        unit.heal(actualHeal)
        val nt = NT_Event_FocusObject()
        nt.`object` = unit.nt()
        nt.screenEffect = NT_Event.EFFECT_HEAL
        consumer?.accept(nt)
        GameUtils.sendUpdate(game, nt)
        ProcessingUtils.pause(DAMAGE_PAUSE)
    }

    fun damage(game: GameContainer, unit: Unit, damage: Int = 1, consumer: Consumer<NT_Event_FocusObject>? = null) {
        Log.trace("UnitControl: damage unit $unit for $damage")
        //dont overkill
        val actualDamage = Math.min(unit.health.value, damage)
        unit.takeDamage(actualDamage)
        val lethal = unit.dead
        if (lethal) {
            //remove unit
            val removed = GameUtils.remove(game, unit)
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
        GameUtils.sendUpdate(game, nt)
        game.updateReceiver.damaged(unit, damage)
        if (lethal) {
            unit.onDeath(null)
            game.updateReceiver.killed(unit, null)
        }
        ProcessingUtils.pause(DAMAGE_PAUSE)
    }

    fun spawn(game: GameContainer, obj: MapObject, location: Location, consumer: Consumer<NT_Event_PlacedObject>? = null) {
        Log.trace("UnitControl: spawn object $obj at $location")
        obj.init(game)
        if (obj.owner.isNeutral()) {
            game.objects += obj
        } else if (obj is Unit) {
            obj.owner.units += obj
        }
        if (obj is Unit) {
            game.buffProcessor.applyGlobalBuffs(obj)
        }
        GameUtils.place(obj, location)
        game.updateReceiver.register(obj)
        val nt = NT_Event_PlacedObject()
        nt.`object` = obj.nt()
        nt.sound = defaultSpawnSound(obj, location)
        consumer?.accept(nt)
        GameUtils.sendUpdate(game, nt)
        game.updateReceiver.spawned(obj, location)
        ProcessingUtils.pause(SPAWN_PAUSE)
    }

    private fun defaultSpawnSound(obj: MapObject, location: Location) =
            if (obj is Monster) {
                "monster.ogg"
            } else {
                "recruit.ogg"
            }

    fun spawnMonsters(game: GameContainer, count: Int) {
        LocationUtils.getRandomFree(game.map.allAreas, count).forEach {
            spawnMonster(game, it as Area)
        }
    }

    fun spawnMonster(game: GameContainer, area: Area) {
        val activeMonsters = game.objects.filterIsInstance<Monster>()
        game.monsterFactory.spawn(game.neutralPlayer, area.type, activeMonsters)?.let {
            Log.debug("spawn monster " + it.name + " on " + area)
            spawn(game, it, area)
        }
    }

    fun conquer(game: GameContainer, units: List<Unit>, target: Location) {
        val owner = PlayerUtils.getOwner(units)
        val targetUnits = if (owner.isNeutral()) {
            target.units
        } else {
            PlayerUtils.getHostileArmy(owner, target)
        }
        if (targetUnits.isEmpty()) {
            move(game, units, target)
        } else {
            game.battleHandler.startBattle(units, targetUnits)
        }
    }

    fun recruit(game: GameContainer, newOwner: Player, units: List<Unit>, newLocation: Location? = null) {
        val recruit = units.filter { it.owner !== newOwner }
        if (recruit.isEmpty()) {
            return
        }
        val moveUnits = recruit.filter { it.alive && newLocation != null && it.location !== newLocation }
        val updateUnits = ListUtils.subtract(recruit, moveUnits)
        recruit.forEach {
            val previousOwner = it.owner
            if (previousOwner.isNeutral()) {
                game.objects.remove(it)
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
            move(game, moveUnits, newLocation!!)
        }
        if (updateUnits.isNotEmpty()) {
            if (newLocation != null) {
                updateUnits.forEach { it.location = newLocation }
            }
            update(game, updateUnits)
        }
    }
}

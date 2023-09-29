package com.broll.gainea.server.core.utils

import com.broll.gainea.net.NT_BoardObject
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
import com.google.common.collect.Lists
import org.apache.commons.collections4.ListUtils
import org.slf4j.LoggerFactory
import java.util.Collections
import java.util.function.Consumer
import java.util.stream.Collectors

object UnitControl {
    private val Log = LoggerFactory.getLogger(UnitControl::class.java)
    private const val MOVE_PAUSE = 1500
    private const val SPAWN_PAUSE = 1500
    private const val DAMAGE_PAUSE = 1000
    fun move(game: GameContainer, unit: MapObject, location: Location) {
        move(game, Lists.newArrayList(unit), location)
    }

    fun move(game: GameContainer, units: List<MapObject>, location: Location) {
        if (units!!.isEmpty()) return
        Log.trace("UnitControl: move units [" + units.size + "] to " + location)
        units.forEach { unit: MapObject -> GameUtils.place(unit, location) }
        val movedObject = NT_Event_MovedObject()
        movedObject.objects = units.stream().map<NT_BoardObject?> { obj: MapObject? -> obj!!.nt() }.toArray<NT_BoardObject> { _Dummy_.__Array__() }
        GameUtils.sendUpdate(game, movedObject)
        game.updateReceiver.moved(units as List<MapObject?>?, location)
        ProcessingUtils.pause(MOVE_PAUSE)
    }

    fun kill(game: GameContainer, unit: Unit) {
        damage(game, unit, Int.MAX_VALUE, null)
    }

    fun focus(game: GameContainer?, `object`: MapObject?, effect: Int) {
        val nt = NT_Event_FocusObject()
        nt.screenEffect = effect
        nt.`object` = `object`!!.nt()
        GameUtils.sendUpdate(game, nt)
        ProcessingUtils.pause(DAMAGE_PAUSE)
    }

    fun focus(game: GameContainer?, objects: List<MapObject?>?, effect: Int) {
        objects!!.stream().map<Location?> { obj: MapObject? -> obj.getLocation() }.distinct().forEach { location: Location? ->
            val nt = NT_Event_FocusObjects()
            nt.screenEffect = effect
            nt.objects = objects.stream().filter { it: MapObject? -> it.getLocation() === location }.map<NT_BoardObject?> { obj: MapObject? -> obj!!.nt() }.toArray<NT_BoardObject> { _Dummy_.__Array__() }
            GameUtils.sendUpdate(game, nt)
            ProcessingUtils.pause(DAMAGE_PAUSE)
        }
    }

    fun update(game: GameContainer?, objects: List<MapObject?>?) {
        val nt = NT_Event_UpdateObjects()
        nt.objects = objects!!.stream().map<NT_BoardObject?> { obj: MapObject? -> obj!!.nt() }.toArray<NT_BoardObject> { _Dummy_.__Array__() }
        GameUtils.sendUpdate(game, nt)
    }


    fun heal(game: GameContainer?, unit: Unit, heal: Int = 1, consumer: Consumer<NT_Event_FocusObject?>? = null) {
        var heal = heal
        Log.trace("UnitControl: heal unit $unit for $heal")
        heal = Math.max(unit.maxHealth.value - unit.health.value, heal)
        unit.heal(heal)
        val nt = NT_Event_FocusObject()
        nt.`object` = unit.nt()
        nt.screenEffect = NT_Event.EFFECT_HEAL
        consumer?.accept(nt)
        GameUtils.sendUpdate(game, nt)
        ProcessingUtils.pause(DAMAGE_PAUSE)
    }

    @JvmOverloads
    fun damage(game: GameContainer, unit: Unit?, damage: Int = 1, consumer: Consumer<NT_Event_FocusObject?>? = null) {
        var damage = damage
        Log.trace("UnitControl: damage unit $unit for $damage")
        //dont overkill
        damage = Math.min(unit.getHealth().value, damage)
        unit!!.takeDamage(damage)
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

    @JvmOverloads
    fun spawn(game: GameContainer?, `object`: MapObject, location: Location?, consumer: Consumer<NT_Event_PlacedObject>? = null) {
        Log.trace("UnitControl: spawn object $`object` at $location")
        if (location == null) {
            Log.error("Cannot spawn object $`object` on null location")
            return
        }
        `object`.init(game)
        val owner = `object`.owner
        owner?.units?.add(`object` as Unit)
                ?: game.getObjects().add(`object`)
        if (`object` is Unit) {
            game.getBuffProcessor().applyGlobalBuffs(`object`)
        }
        GameUtils.place(`object`, location)
        game.getUpdateReceiver().register(`object`)
        val nt = NT_Event_PlacedObject()
        nt.`object` = `object`.nt()
        nt.sound = defaultSpawnSound(`object`, location)
        consumer?.accept(nt)
        GameUtils.sendUpdate(game, nt)
        game.getUpdateReceiver().spawned(`object`, location)
        ProcessingUtils.pause(SPAWN_PAUSE)
    }

    private fun defaultSpawnSound(`object`: MapObject, location: Location): String {
        var sound: String? = null
        sound = if (`object` is Monster) {
            "monster.ogg"
        } else {
            "recruit.ogg"
        }
        return sound
    }

    fun spawnMonsters(game: GameContainer?, count: Int) {
        val areas = game.getMap().allAreas
        Collections.shuffle(areas)
        val freeAreas = areas!!.stream().filter { obj: Area? -> obj!!.isFree }.iterator()
        var spawned = 0
        while (spawned < count && freeAreas.hasNext()) {
            spawnMonster(game, freeAreas.next())
            spawned++
        }
    }

    fun spawnMonster(game: GameContainer?, area: Area?) {
        val activeMonsters = game.getObjects().stream().filter { it: MapObject? -> it is Monster }.map { it: MapObject? -> it as Monster? }.collect(Collectors.toList())
        val monster = game.getMonsterFactory().spawn(area.getType(), activeMonsters)
        if (monster != null) {
            Log.debug("spawn monster " + monster.name + " on " + area)
            spawn(game, monster, area)
        }
    }

    fun conquer(game: GameContainer, units: List<Unit>, target: Location) {
        val owner = PlayerUtils.getOwner(units)
        val targetUnits: List<Unit?>?
        targetUnits = if (owner == null) {
            LocationUtils.getUnits(target)
        } else {
            PlayerUtils.getHostileArmy(owner, target)
        }
        if (targetUnits.isEmpty()) {
            move(game, units, target)
        } else {
            game.battleHandler.startBattle(units, targetUnits)
        }
    }

    @JvmOverloads
    fun recruit(game: GameContainer, newOwner: Player, units: List<Unit?>, newLocation: Location? = null) {
        val recruit = units.stream().filter { it: Unit? -> it.getOwner() !== newOwner }.collect(Collectors.toList())
        if (recruit.isEmpty()) {
            return
        }
        val moveUnits = recruit.stream().filter { it: Unit? -> it!!.alive && newLocation != null && it.location !== newLocation }.collect(Collectors.toList())
        val updateUnits = ListUtils.subtract(recruit, moveUnits)
        recruit.forEach(Consumer { it: Unit? ->
            val previousOwner = it.getOwner()
            previousOwner?.units?.remove(it) ?: game.objects.remove(it)
            newOwner.units.add(it)
            it.setOwner(newOwner)
            if (it!!.dead) {
                it.heal()
            }
            if (it is Monster) {
                it.removeActionTimer()
            }
        })
        if (!moveUnits.isEmpty()) {
            move(game, moveUnits, newLocation)
        }
        if (!updateUnits.isEmpty()) {
            if (newLocation != null) {
                updateUnits.forEach(Consumer { it: Unit? -> it.setLocation(newLocation) })
            }
            update(game, updateUnits)
        }
    }
}

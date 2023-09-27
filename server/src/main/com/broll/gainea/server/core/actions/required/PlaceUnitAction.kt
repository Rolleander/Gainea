package com.broll.gainea.server.core.actions.required

import com.broll.gainea.net.NT_Action_PlaceUnit
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.actions.AbstractActionHandler
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.actions.RequiredActionContext
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.LocationUtils
import com.broll.gainea.server.core.utils.UnitControl
import org.apache.commons.lang3.tuple.Pair
import org.slf4j.LoggerFactory

class PlaceUnitAction : AbstractActionHandler<NT_Action_PlaceUnit, PlaceUnitAction.Context>() {
    var unitToPlace: Unit? = null
        private set

    inner class Context(action: NT_Action_PlaceUnit,
                        val unitToPlace: Unit,
                        val locations: List<Location>,
                        var selectedLocation: Location
            ) : ActionContext<NT_Action_PlaceUnit>(action)


    fun placeSoldier(player: Player, locations: List<Location> = player.controlledLocations): Pair<Unit, Location> {
        return placeUnit(player, createSoldier(player), locations, "Platziere einen Soldat")
    }

    fun placeCommander(player: Player, locations: List<Location> = player.controlledLocations): Pair<Unit, Location> {
        return placeUnit(player, createCommander(player), locations, "Platziere deinen Feldherr")
    }
        
    private fun createSoldier(player: Player): Soldier {
        return player.fraction.createSoldier()
    }

    private fun createCommander(player: Player): Soldier {
        return player.fraction.createCommander()
    }

    fun placeUnit(player: Player, unit: Unit, locations: List<Location>, message: String): Pair<Unit, Location> {
        if (locations.isEmpty()) {
            throw RuntimeException("Invalid place unit context: list of locations is empty")
        }
        Log.debug("Place unit action")
        val placeUnit = NT_Action_PlaceUnit()
        placeUnit.unitToPlace = unit.nt()
        placeUnit.possibleLocations = LocationUtils.getLocationNumbers(locations)
        val context = Context(placeUnit,  unit,locations, locations[0])
        unitToPlace = context.unitToPlace
        actionHandlers.reactionActions.requireAction(player, RequiredActionContext(context, message))
        Log.trace("Wait for place unit reaction")
        processingBlock.waitFor(player)
        UnitControl.spawn(game, context.unitToPlace, context.selectedLocation)
        return Pair.of(unit, context.selectedLocation)
    }

    override fun handleReaction(context: Context, action: NT_Action_PlaceUnit, reaction: NT_Reaction) {
        Log.trace("Handle place unit reaction")
        val nr = reaction.option
        val location = context.locations[nr]
        context.selectedLocation = location
        processingBlock.resume()
    }

    companion object {
        private val Log = LoggerFactory.getLogger(PlaceUnitAction::class.java)
    }
}

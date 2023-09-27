package com.broll.gainea.server.core.actions.optional

import com.broll.gainea.net.NT_Action_Move
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.actions.AbstractActionHandler
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.MapObject
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.UnitControl
import org.slf4j.LoggerFactory
import java.util.function.Consumer

class MoveUnitAction : AbstractActionHandler<NT_Action_Move, MoveUnitAction.Context>() {
    inner class Context(action: NT_Action_Move,   val unitsToMove: MutableList<Unit>,  val location: Location)
        : ActionContext<NT_Action_Move>(action)

    fun move(objects: List<Unit>, location: Location): Context {
        val moveUnit = NT_Action_Move()
        moveUnit.location = location.number.toShort()
        moveUnit.units = objects.map { it.nt() }.toTypedArray()
        return Context(moveUnit, objects.toMutableList() ,location)
    }

    override fun handleReaction(context: Context, action: NT_Action_Move, reaction: NT_Reaction) {
        game.processingCore.execute {
            Log.trace("Handle move reaction")
            val selectedUnits = mutableListOf<MapObject>()
            var from: Location? = null
            for (selection in reaction.options) {
                val unit = context.unitsToMove[selection]
                if (from == null) {
                    from = unit.location
                } else if (unit.location !== from) {
                    continue
                }
                selectedUnits.add(unit)
            }
            context.unitsToMove.removeAll(selectedUnits)
            //perform move
            selectedUnits.forEach { it.moved() }
            UnitControl.move(game, selectedUnits, context.location)
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(MoveUnitAction::class.java)
    }
}

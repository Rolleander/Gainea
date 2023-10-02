package com.broll.gainea.server.core.actions.optional

import com.broll.gainea.net.NT_Action_Attack
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.actions.AbstractActionHandler
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.utils.getHostileArmy
import org.slf4j.LoggerFactory

class AttackAction : AbstractActionHandler<NT_Action_Attack, AttackAction.Context>() {
    inner class Context(action: NT_Action_Attack, val attackers: MutableList<Unit>, val location: Location)
        : ActionContext<NT_Action_Attack>(action)

    fun attack(attackers: List<Unit>, attackLocation: Location): Context {
        val action = NT_Action_Attack()
        action.units = attackers.map { it.nt() }.toTypedArray()
        action.location = attackLocation.number.toShort()
        return Context(action, attackers.toMutableList(), attackLocation)
    }

    override fun handleReaction(context: Context, action: NT_Action_Attack, reaction: NT_Reaction) {
        game.processingCore.execute {
            Log.trace("Handle attack reaction")
            val selectedAttackers = mutableListOf<Unit>()
            var from: Location? = null
            reaction.options.map { selection -> context.attackers.find { it.id == selection } }.filterNotNull().forEach { unit ->
                if (from == null) {
                    from = unit.location
                }
                if (unit.location == from) {
                    selectedAttackers.add(unit)
                }
            }
            context.attackers.removeAll(selectedAttackers)
            if (selectedAttackers.isNotEmpty()) {
                selectedAttackers.forEach { it.attacked() }
                startFight(selectedAttackers, context.location)
            }
        }
    }

    private fun startFight(attackers: List<Unit>, attackLocation: Location) {
        val defenders = player.getHostileArmy(attackLocation)
        game.battleHandler.startBattle(attackers, defenders)
    }

    companion object {
        private val Log = LoggerFactory.getLogger(AttackAction::class.java)
    }
}

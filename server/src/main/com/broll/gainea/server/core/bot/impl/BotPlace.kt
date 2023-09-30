package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Action_PlaceUnit
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.bot.BotAction
import com.broll.gainea.server.core.bot.BotUtils

class BotPlace : BotAction<NT_Action_PlaceUnit>() {
    override fun react(action: NT_Action_PlaceUnit, reaction: NT_Reaction) {
        if (action.possibleLocations.size == 1) {
            reaction.option = 0
            return
        }
        val handler = game.reactionHandler.actionHandlers.getHandler(PlaceUnitAction::class.java)
        val locations = BotUtils.getLocations(game, action.possibleLocations)
        val index = strategy.chooseUnitPlace(handler.unitToPlace!!, locations)
        reaction.option = index
    }

    override val actionClass: Class<out NT_Action>
        get() = NT_Action_PlaceUnit::class.java
}

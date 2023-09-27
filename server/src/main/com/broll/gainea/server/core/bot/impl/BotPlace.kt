package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Actionimport

com.broll.gainea.net.NT_Action_PlaceUnitimport com.broll.gainea.net.NT_Reactionimport com.broll.gainea.server.core.actions.required.PlaceUnitActionimport com.broll.gainea.server.core.bot.BotActionimport com.broll.gainea.server.core.bot.BotUtils
class BotPlace : BotAction<NT_Action_PlaceUnit>() {
    override fun react(action: NT_Action_PlaceUnit, reaction: NT_Reaction) {
        if (action.possibleLocations.size == 1) {
            reaction.option = 0
            return
        }
        val handler = game.reactionHandler.actionHandlers.getHandler(PlaceUnitAction::class.java)
        val locations = BotUtils.getLocations(game!!, action.possibleLocations)
        val index = strategy!!.chooseUnitPlace(handler.unitToPlace, locations)
        reaction.option = index
    }

    override val actionClass: Class<out NT_Action?>?
        get() = NT_Action_PlaceUnit::class.java
}

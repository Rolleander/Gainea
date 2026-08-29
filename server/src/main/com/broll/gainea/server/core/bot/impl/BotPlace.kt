package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.proto.NT_Action_PlaceUnit
import com.broll.gainea.net.proto.NT_Reaction
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.bot.BotAction
import com.broll.gainea.server.core.bot.BotUtils
import com.google.protobuf.Message

class BotPlace : BotAction<NT_Action_PlaceUnit>() {
    override fun react(action: NT_Action_PlaceUnit, reaction: NT_Reaction.Builder) {
        if (action.possibleLocationsCount == 1) {
            reaction.setOption(0)
            return
        }
        val handler = game.reactionHandler.actionHandlers.getHandler(PlaceUnitAction::class.java)
        val locations = BotUtils.getLocations(game, action.possibleLocationsList)
        val index = strategy.chooseUnitPlace(handler.unitToPlace!!, locations)
        reaction.setOption(index)
    }

    override val actionClass: Class<out Message>
        get() = NT_Action_PlaceUnit::class.java
}

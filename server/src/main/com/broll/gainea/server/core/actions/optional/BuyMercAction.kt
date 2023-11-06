package com.broll.gainea.server.core.actions.optional

import com.broll.gainea.net.NT_Action_BuyMerc
import com.broll.gainea.net.NT_Event_BoughtMerc
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.actions.AbstractActionHandler
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.sendUpdate
import org.slf4j.LoggerFactory

class BuyMercAction : AbstractActionHandler<NT_Action_BuyMerc, BuyMercAction.Context>() {
    inner class Context(
        action: NT_Action_BuyMerc,
        val player: Player
    ) : ActionContext<NT_Action_BuyMerc>(action)

    fun purchasableMerc(player: Player, index: Int): Context {
        val action = NT_Action_BuyMerc()
        action.index = index
        return Context(action, player)
    }

    override fun handleReaction(
        context: Context,
        action: NT_Action_BuyMerc,
        reaction: NT_Reaction
    ) {
        game.processingCore.execute { buyMerc(context.player, action.index) }
    }

    private fun buyMerc(player: Player, index: Int) {
        Log.trace("Buy merc reaction")
        with(player.mercenaryShop.units.get(index)) {
            if (!available || player.goalHandler.stars < price) {
                Log.error("Not allowed to buy")
                return
            }
            val handler =
                game.reactionHandler.actionHandlers.getHandler(PlaceUnitAction::class.java)
            handler.placeUnit(player, unit, player.controlledLocations.toList(), "Wähle einen Ort")
            player.goalHandler.removeStars(price)
            val nt = NT_Event_BoughtMerc()
            nt.unit = unit.nt()
            nt.player = player.serverPlayer.id
            game.sendUpdate(nt)
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(BuyMercAction::class.java)
    }
}

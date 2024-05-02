package com.broll.gainea.server.core.actions.optional

import com.broll.gainea.net.NT_Action_Shop
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.actions.AbstractActionHandler
import com.broll.gainea.server.core.actions.ActionContext
import com.broll.gainea.server.core.player.Player
import org.slf4j.LoggerFactory

class ShopAction : AbstractActionHandler<NT_Action_Shop, ShopAction.Context>() {
    inner class Context(
        action: NT_Action_Shop,
        val player: Player
    ) : ActionContext<NT_Action_Shop>(action)

    fun purchasableItem(player: Player, index: Int): Context {
        val action = NT_Action_Shop()
        action.index = index
        return Context(action, player)
    }

    override fun handleReaction(
        context: Context,
        action: NT_Action_Shop,
        reaction: NT_Reaction
    ) {
        game.processingCore.execute { buyMerc(context.player, action.index) }
    }

    private fun buyMerc(player: Player, index: Int) {
        Log.trace("Shop reaction")
        with(player.shop.items[index]) {
            if (!available || player.goalHandler.stars < price) {
                Log.error("Not allowed to buy")
                return
            }
            player.goalHandler.removeStars(price)
            purchased(game, player)
        }
    }

    companion object {
        private val Log = LoggerFactory.getLogger(ShopAction::class.java)
    }
}

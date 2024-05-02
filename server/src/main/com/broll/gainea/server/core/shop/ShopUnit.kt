package com.broll.gainea.server.core.shop

import com.broll.gainea.net.NT_Event_BoughtMerc
import com.broll.gainea.net.NT_ShopItem
import com.broll.gainea.net.NT_ShopUnit
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.getActionHandler
import com.broll.gainea.server.core.utils.sendUpdate

data class ShopUnit(
    override val price: Int,
    val unit: Unit,
    override val available: Boolean = true
) : ShopItem {
    override fun nt(): NT_ShopItem {
        val item = NT_ShopUnit()
        item.price = price.toShort()
        item.unit = unit.nt()
        return item;
    }

    override fun purchased(game: Game, player: Player) {
        val handler = game.getActionHandler(PlaceUnitAction::class.java)
        handler.placeUnit(player, unit, player.controlledLocations.toList())
        val nt = NT_Event_BoughtMerc()
        nt.unit = unit.nt()
        nt.player = player.serverPlayer.id
        game.sendUpdate(nt)
    }
}
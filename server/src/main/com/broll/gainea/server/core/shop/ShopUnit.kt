package com.broll.gainea.server.core.shop

import com.broll.gainea.net.proto.NT_Event
import com.broll.gainea.net.proto.NT_Event_BoughtMerc
import com.broll.gainea.net.proto.NT_ShopItem
import com.broll.gainea.net.proto.NT_ShopUnit
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
        val shopUnit = NT_ShopUnit.newBuilder().setUnit(unit.nt()).build()
        return NT_ShopItem.newBuilder().setPrice(price).setUnit(shopUnit).build()
    }

    override fun purchased(game: Game, player: Player) {
        val handler = game.getActionHandler(PlaceUnitAction::class.java)
        handler.placeUnit(player, unit, player.controlledLocations.toList())
        game.sendUpdate(
            NT_Event_BoughtMerc.newBuilder().setUnit(unit.nt())
                .setBase(NT_Event.newBuilder().setPlayer(player.serverPlayer.id).build())
                .build()
        )
    }
}

package com.broll.gainea.server.core.shop

import com.broll.gainea.net.NT_ShopItem
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player

interface ShopItem {
    val price: Int
    val available: Boolean
    fun nt(): NT_ShopItem
    fun purchased(game: Game, player: Player)
}
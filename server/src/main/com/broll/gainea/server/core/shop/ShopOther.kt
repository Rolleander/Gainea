package com.broll.gainea.server.core.shop

import com.broll.gainea.net.NT_ShopItem
import com.broll.gainea.net.NT_ShopOther

abstract class ShopOther(
    override val price: Int,
    val description: String,
    override val available: Boolean = true
) : ShopItem {
    override fun nt(): NT_ShopItem {
        val item = NT_ShopOther()
        item.price = price.toShort()
        item.description = description
        return item;
    }

}
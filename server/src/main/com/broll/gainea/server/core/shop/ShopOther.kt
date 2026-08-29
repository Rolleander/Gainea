package com.broll.gainea.server.core.shop

import com.broll.gainea.net.proto.NT_ShopItem
import com.broll.gainea.net.proto.NT_ShopOther

abstract class ShopOther(
    override val price: Int,
    val description: String,
    override val available: Boolean = true
) : ShopItem {
    override fun nt(): NT_ShopItem {
        val other = NT_ShopOther.newBuilder().setDescription(description).build()
        return NT_ShopItem.newBuilder().setPrice(price).setOther(other).build()
    }

}

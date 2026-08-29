package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.proto.NT_Action_Shop
import com.broll.gainea.net.proto.NT_Reaction
import com.broll.gainea.server.core.bot.BotOptionalAction
import com.broll.gainea.server.core.bot.BotOptionalAction.BotOption
import com.google.protobuf.Message

class BotShop : BotOptionalAction<NT_Action_Shop, BotOption>() {
    override fun react(action: NT_Action_Shop, reaction: NT_Reaction.Builder) {

    }

    override val actionClass: Class<out Message>
        get() = NT_Action_Shop::class.java

    override fun score(action: NT_Action_Shop): BotOption? {
        return null
    }
}

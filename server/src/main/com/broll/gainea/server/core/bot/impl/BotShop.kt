package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Action_BuyMerc
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.server.core.bot.BotOptionalAction
import com.broll.gainea.server.core.bot.BotOptionalAction.BotOption

class BotShop : BotOptionalAction<NT_Action_BuyMerc, BotOption>() {
    override fun react(action: NT_Action_BuyMerc, reaction: NT_Reaction) {

    }

    override val actionClass: Class<out NT_Action>
        get() = NT_Action_BuyMerc::class.java

    override fun score(action: NT_Action_BuyMerc): BotOption? {
        return null
    }
}

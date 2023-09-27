package com.broll.gainea.server.core.bot

import com.broll.gainea.server.core.bot.BotOptionalAction.BotOptionimport

com.broll.gainea.server.core.bot.impl .BotSelect com.broll.gainea.server.core.objects.Unit
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListenerimport

java.util.ArrayList
class CardOption : BotOption {
    private val selectOptions: MutableList<BotSelect.Selection?> = ArrayList()

    constructor(score: Float) : super(score)
    constructor() : super(20f)

    fun select(option: BotSelect.Selection?) {
        selectOptions.add(option)
    }

    fun selectStrongestUnit(select: BotSelect?) {
        selectOptions.add(select!!.selectUnit { units: List<Unit> -> BotUtils.getHighestScoreEntry(units) { obj: Unit -> obj.battleStrength } })
    }

    fun getSelectOptions(): List<BotSelect.Selection?> {
        return selectOptions
    }
}

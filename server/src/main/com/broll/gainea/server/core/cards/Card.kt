package com.broll.gainea.server.core.cards

import com.broll.gainea.net.NT_Card
import com.broll.gainea.net.NT_Cardimport

com.broll.gainea.server.core.GameContainerimport com.broll.gainea.server.core.actions.ActionHandlersimport com.broll.gainea.server.core.actions.required.PlaceUnitActionimport com.broll.gainea.server.core.actions.required.SelectChoiceAction com.broll.gainea.server.core.player.Player
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListener

abstract class Card(val picture: Int, val title: String, val text: String) {
    protected var game: GameContainer? = null
    protected var owner: Player? = null
    var id = 0
        private set
    protected var actions: ActionHandlers? = null
    protected var placeUnitHandler: PlaceUnitAction? = null
    protected var selectHandler: SelectChoiceAction? = null
    var drawChance = 1f
        protected set

    fun init(game: GameContainer?, owner: Player?, id: Int) {
        this.game = game
        this.owner = owner
        this.id = id
    }

    abstract val isPlayable: Boolean
    fun play(actionHandlers: ActionHandlers?) {
        actions = actionHandlers
        placeUnitHandler = actions!!.getHandler(PlaceUnitAction::class.java)
        selectHandler = actions!!.getHandler(SelectChoiceAction::class.java)
        play()
    }

    protected abstract fun play()
    open fun nt(): NT_Card {
        val card = NT_Card()
        card.id = id.toShort()
        card.text = text
        card.picture = picture.toShort()
        card.title = title
        return card
    }

    override fun toString(): String {
        return super.toString() + "{" +
                "owner=" + owner +
                ", title='" + title + '\'' +
                '}'
    }
}

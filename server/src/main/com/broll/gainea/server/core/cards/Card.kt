package com.broll.gainea.server.core.cards

import com.broll.gainea.net.NT_Card
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.actions.required.SelectChoiceAction
import com.broll.gainea.server.core.player.Player

abstract class Card(val picture: Int, val title: String, val text: String) {
    var id = 0
        private set
    protected lateinit var game: Game
    protected lateinit var owner: Player
    protected lateinit var actions: ActionHandlers
    protected lateinit var placeUnitHandler: PlaceUnitAction
    protected lateinit var selectHandler: SelectChoiceAction
    var drawChance = 1f
        protected set

    fun init(game: Game, owner: Player, id: Int) {
        this.game = game
        this.owner = owner
        this.id = id
    }

    open fun validFor(game: Game): Boolean = true

    abstract val isPlayable: Boolean
    fun play(actionHandlers: ActionHandlers) {
        actions = actionHandlers
        placeUnitHandler = actions.getHandler(PlaceUnitAction::class.java)
        selectHandler = actions.getHandler(SelectChoiceAction::class.java)
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

package com.broll.gainea.server.core.cards

import com.broll.gainea.core.cards.EffectType
import com.broll.gainea.net.proto.NT_Card
import com.broll.gainea.net.proto.NT_Event
import com.broll.gainea.net.proto.NT_Event_PlayedCard
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.actions.optional.CardAction
import com.broll.gainea.server.core.actions.required.PlaceUnitAction
import com.broll.gainea.server.core.actions.required.SelectChoiceAction
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.player.isNeutral
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.sendUpdate

abstract class Card(
    val picture: Int,
    val effectType: EffectType,
    val title: String,
    val text: String,
) {
    var id = 0
        private set
    protected lateinit var game: Game
    lateinit var owner: Player
    protected lateinit var actions: ActionHandlers
    protected lateinit var placeUnitHandler: PlaceUnitAction
    protected lateinit var selectHandler: SelectChoiceAction
    var drawChance = 1f
        protected set

    fun init(game: Game, owner: Player) {
        this.game = game
        this.owner = owner
        this.id = game.newObjectId()
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
        return NT_Card.newBuilder().setId(id).setText(text).setPicture(picture)
            .setTitle(title).setEvent(this is DirectlyPlayedCard).build()
    }

    override fun toString(): String {
        return super.toString() + "{" +
                "owner=" + owner +
                ", title='" + title + '\'' +
                '}'
    }
}

fun Game.playCard(card: Card) {
    val playerId = if (card.owner.isNeutral()) -1 else card.owner.serverPlayer.id
    val sound = if (card is DirectlyPlayedCard) "glitter.ogg" else "dwooom.ogg"
    sendUpdate(
        NT_Event_PlayedCard.newBuilder()
            .setBase(NT_Event.newBuilder().setPlayer(playerId).setSound(sound).build())
            .setCard(card.nt())
            .build()
    )
    card.owner.cardHandler.cards.remove(card)
    ProcessingUtils.pause(CardAction.PLAY_CARD_DELAY)
    card.play(reactionHandler.actionHandlers)
    updateReceiver.playedCard(card)
}

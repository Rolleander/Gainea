package com.broll.gainea.server.core.player;

import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedCard;
import com.broll.gainea.net.NT_Event_ReceivedCard;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.optional.CardAction;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.utils.GameUtils;

import java.util.ArrayList;
import java.util.List;

public class CardHandler {

    private List<Card> cards = new ArrayList<>();
    private GameContainer game;
    private Player player;

    public CardHandler(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void drawRandomCard() {
        game.getCardStorage().drawRandomCard(player);
    }

    public List<Card> getCards() {
        return cards;
    }

    public void receiveCard(Card card) {
        card.init(game, player, game.newObjectId());
        if (card instanceof DirectlyPlayedCard) {
            game.getReactionHandler().getActionHandlers().getHandler(CardAction.class).playCard(player, card);
            return;
        }
        this.cards.add(card);
        NT_Event_ReceivedCard nt = new NT_Event_ReceivedCard();
        nt.card = card.nt();
        nt.sound = "chime.ogg";
        NT_Event_OtherPlayerReceivedCard nt2 = new NT_Event_OtherPlayerReceivedCard();
        nt2.player = player.getServerPlayer().getId();
        GameUtils.sendUpdate(game, player, nt, nt2);
    }

    public void discardCard(Card card) {
        this.cards.remove(card);
    }

    public int getCardCount() {
        return this.cards.size();
    }

    public NT_Card[] ntCards() {
        return cards.stream().map(Card::nt).toArray(NT_Card[]::new);
    }

}

package com.broll.gainea.server.core.cards;

import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Event_OtherPlayerReceivedCard;
import com.broll.gainea.net.NT_Event_ReceivedCard;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.actions.optional.CardAction;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUtils;

import java.util.List;
import java.util.ArrayList;

public class CardHandler {

    private List<AbstractCard> cards = new ArrayList<>();
    private GameContainer game;
    private Player player;

    public CardHandler(GameContainer game, Player player) {
        this.game = game;
        this.player = player;
    }

    public void drawRandomCard() {
        game.getCardStorage().drawRandomCard(player);
    }

    public List<AbstractCard> getCards() {
        return cards;
    }

    public void receiveCard(AbstractCard card) {
        if (card instanceof DirectlyPlayedCard) {
            game.getReactionHandler().getActionHandlers().getHandler(CardAction.class).playCard(player, card);
            return;
        }
        this.cards.add(card);
        NT_Event_ReceivedCard nt = new NT_Event_ReceivedCard();
        nt.card = card.nt();
        NT_Event_OtherPlayerReceivedCard nt2 = new NT_Event_OtherPlayerReceivedCard();
        nt2.player = player.getServerPlayer().getId();
        GameUtils.sendUpdate(game, player, nt, nt2);
    }

    public void discardCard(AbstractCard card) {
        this.cards.remove(card);
    }

    public int getCardCount() {
        return this.cards.size();
    }

    public NT_Card[] ntCards() {
        return cards.stream().map(AbstractCard::nt).toArray(NT_Card[]::new);
    }
}

package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.MessageUtils;

import java.util.List;

public class C_Secrets extends Card {
    public C_Secrets() {
        super(40, "Bestechung", "Schaut euch die Ziele und Aktionskarten eines beliebigen Spielers an");
        setDrawChance(0.8f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Player player = selectHandler.selectOtherPlayer(owner, "Welchen Spieler bestechen?");
        List<Card> cards = player.getCardHandler().getCards();
        List<AbstractGoal> goals = player.getGoalHandler().getGoals();
        String name = player.getServerPlayer().getName();
        String text = name + "'s Ziele: \n";
        for (AbstractGoal goal : goals) {
            text += " - " + goal.getText() + " ( " +goal.getDifficulty().getPoints()+"P "  + goal.getRestrictionInfo() + ")\n";
        }
        text += name + "'s Aktionskarten: \n";
        for (Card card : cards) {
            text += " - " + card.getTitle() + "\n";
        }
        MessageUtils.displayMessage(game, owner, text);
    }

}

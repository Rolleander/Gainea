package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.MessageUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_Secrets extends AbstractCard {
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
        List<AbstractCard> cards = player.getCardHandler().getCards();
        List<AbstractGoal> goals = player.getGoalHandler().getGoals();
        String name = player.getServerPlayer().getName();
        String text = name + "'s Ziele: \n";
        for (AbstractGoal goal : goals) {
            text += " - " + goal.getText() + " ( " +goal.getDifficulty().getPoints()+"P "  + goal.getRestrictionInfo() + ")\n";
        }
        text += name + "'s Aktionskarten: \n";
        for (AbstractCard card : cards) {
            text += " - " + card.getTitle() + "\n";
        }
        MessageUtils.displayMessage(game, owner, text);
    }

}

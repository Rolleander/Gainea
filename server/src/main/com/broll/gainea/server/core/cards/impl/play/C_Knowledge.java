package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.google.common.collect.Lists;

public class C_Knowledge extends Card {

    private final static int STARS = 5;

    public C_Knowledge() {
        super(29, "Bürokratie", "Wählt einer der folgenden Effekte: \n\n- Platziert einen Soldaten \n- Erhaltet " + STARS + " Sterne \n- Erhaltet eine zufällige Aktionskarte");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        int option = selectHandler.selection("Wählt einen Effekt",
                Lists.newArrayList("Platziert einen Soldaten", "Erhaltet " + STARS + " Sterne", "Erhaltet eine zufällige Aktionskarte"));
        switch (option) {
            case 0:
                placeUnitHandler.placeSoldier(owner);
                break;
            case 1:
                owner.getGoalHandler().addStars(STARS);
                break;
            case 2:
                owner.getCardHandler().drawRandomCard();
                break;
        }
    }

}

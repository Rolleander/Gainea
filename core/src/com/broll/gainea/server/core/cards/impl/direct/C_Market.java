package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class C_Market extends DirectlyPlayedCard {

    private final static int COUNT = 5;

    public C_Market() {
        super(30, "Jahrmarkt", "Verursacht 1 Schaden an " + COUNT + " zufälligen Einheiten im Spiel");
    }

    @Override
    protected void play() {

    }

}

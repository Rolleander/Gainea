package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class C_Thunder extends DirectlyPlayedCard {

    private final static int COUNT = 7;

    public C_Thunder() {
        super(50, "Donnerschauer", "Verursacht 1 Schaden an " + COUNT + " zufälligen Einheiten im Spiel");
    }

    @Override
    protected void play() {
        List<Unit> units = new ArrayList<>();
        units.addAll(GameUtils.getUnits(game.getObjects()));
        game.getActivePlayers().stream().map(Player::getUnits).forEach(units::addAll);
        Collections.shuffle(units);
        for (int i = 0; i < COUNT && i < units.size(); i++) {
            UnitControl.damage(game, units.get(i), 1);
        }
    }

}

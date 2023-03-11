package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.stream.Collectors;

public class C_RescueFlight extends Card {
    public C_RescueFlight() {
        super(73, "Luftrettung", "Bewegt eine eurer Einheiten auf ein beliebiges freies Feld");
        setDrawChance(0.3f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Unit unit = SelectionUtils.selectPlayerUnit(game, owner, "Welche Einheit soll bewegt werden?");
        Location target = selectHandler.selectLocation("Wohin bewegen?", game.getMap().getAllLocations().stream().filter(Location::isFree).collect(Collectors.toList()));
        UnitControl.move(game, unit, target);
    }

}

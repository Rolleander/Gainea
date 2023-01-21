package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_TeleportGate extends Card {
    public C_TeleportGate() {
        super(56, "Teleporter", "Teleportiert eine eurer Einheiten auf ein beliebiges freies Feld");
        setDrawChance(0.3f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        BattleObject unit = SelectionUtils.selectPlayerUnit(game, owner, "Welche Einheit soll teleportiert werden?");
        List<Location> locations = game.getMap().getAllLocations().stream().filter(Location::isFree).collect(Collectors.toList());
        Location target = selectHandler.selectLocation("", locations);
        UnitControl.move(game, unit, target);
    }

}

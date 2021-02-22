package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class C_Rooting extends AbstractCard {

    private final static int DURATION = 3;

    public C_Rooting() {
        super(63, "Schattenfesseln", "Wählt eine feindliche Truppe. Diese kann sich für " + DURATION + " Runden nicht bewegen.");
        setDrawChance(0.7f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Location location = selectHandler.selectLocation("Ziel für wählen", new ArrayList<>(PlayerUtils.getHostileLocations(game, owner)));
        List<MapObject> units = location.getInhabitants().stream().collect(Collectors.toList());
        IntBuff rootDebuff = new IntBuff(BuffType.SET,0);
        units.forEach(unit->{
            unit.getMovesPerTurn().addBuff(rootDebuff);
        });
        game.getBuffDurationProcessor().timeoutBuff(rootDebuff, DURATION);
    }

}

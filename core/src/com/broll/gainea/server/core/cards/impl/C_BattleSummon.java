package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.List;

public class C_BattleSummon extends AbstractCard {

    public C_BattleSummon() {
        super(66, "Rachedämon", "Beschwört einen Rachedämon (5/3) der nächste Runde stirbt.");
        setDrawChance(0.6f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        List<Location> locations = owner.getControlledLocations();
        Soldier demon = owner.getFraction().createSoldier();
        demon.setStats(5, 0);
        demon.setIcon(125);
        demon.setName("Rachedämon");
        IntBuff buff = new IntBuff(BuffType.ADD, 3);
        demon.getHealth().addBuff(buff);
        placeUnitHandler.placeUnit(owner, demon, locations, "Wählt einen Ort für die Beschwörung");
        game.getBuffDurationProcessor().timeoutBuff(buff, 1);
    }

}

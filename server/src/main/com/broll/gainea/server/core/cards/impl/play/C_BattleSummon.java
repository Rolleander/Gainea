package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;

import java.util.List;

public class C_BattleSummon extends Card {

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
        demon.addHealthBuff(buff);
        demon.turnStart();
        placeUnitHandler.placeUnit(owner, demon, locations, "Wählt einen Ort für die Beschwörung");
        game.getBuffProcessor().timeoutBuff(buff, 1);
    }

}

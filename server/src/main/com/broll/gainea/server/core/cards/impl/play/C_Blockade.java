package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_Blockade extends Card {

    private final static int ROUNDS = 7;

    public C_Blockade() {
        super(54, "Burgfried", "Platziert eine neutrale Befestigung (3/10) auf ein beliebiges freies Feld. Sie zerfällt nach " + ROUNDS + " Runden.");
        setDrawChance(0.4f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Blockade soldier = new Blockade();
        IntBuff buff = new IntBuff(BuffType.ADD, 10);
        soldier.addHealthBuff(buff);
        List<Area> locations = game.getMap().getAllAreas().stream().filter(Location::isFree).collect(Collectors.toList());
        Location location = selectHandler.selectLocation("Wo soll die Befestigung errichtet werden?", locations);
        UnitControl.spawn(game, soldier, location);
        game.getBuffProcessor().timeoutBuff(buff, ROUNDS);
    }

    private class Blockade extends Unit {
        public Blockade() {
            super(null);
            setIcon(127);
            setName("Befestigung");
            setStats(3, 0);
        }
    }

}

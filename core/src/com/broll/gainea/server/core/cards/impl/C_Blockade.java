package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_Blockade extends AbstractCard {

    private final static int TURNS = 7;

    public C_Blockade() {
        super(54, "Einsamer Landstreicher", "Platziert eine neutrale Befestigung (3/10) auf ein beliebiges freies Feld. Sie zerfällt nach " + TURNS + " Runden.");
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
        soldier.getHealth().addBuff(buff);
        List<Area> locations = game.getMap().getAllAreas().stream().filter(Location::isFree).collect(Collectors.toList());
        Location location = selectHandler.selectLocation("Wo soll die Befestigung errichtet werden?", locations);
        UnitControl.spawn(game, soldier, location);
        game.getBuffDurationProcessor().timeoutBuff(buff, TURNS);
    }

    private class Blockade extends BattleObject {
        public Blockade() {
            super(null);
            setIcon(127);
            setName("Befestigung");
            setStats(3, 0);
        }
    }

}

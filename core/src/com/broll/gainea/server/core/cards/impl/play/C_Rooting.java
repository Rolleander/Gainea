package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

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
        Location location = selectHandler.selectLocation("Ziel für Schattenfesseln wählen", new ArrayList<>(PlayerUtils.getHostileLocations(game, owner)));
        List<MapObject> units = location.getInhabitants().stream().collect(Collectors.toList());
        IntBuff rootDebuff = new IntBuff(BuffType.SET,0);
        units.forEach(unit->{
            unit.getMovesPerTurn().addBuff(rootDebuff);
            UnitControl.focus(game, unit, NT_Abstract_Event.EFFECT_DEBUFF);
        });
        game.getBuffProcessor().timeoutBuff(rootDebuff, DURATION);
    }

}

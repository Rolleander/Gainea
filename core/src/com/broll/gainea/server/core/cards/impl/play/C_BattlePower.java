package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Abstract_Event;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.buffs.BuffType;
import com.broll.gainea.server.core.objects.buffs.IntBuff;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;

public class C_BattlePower extends AbstractCard {
    public C_BattlePower() {
        super(22, "Sturmangriff", "Verleiht allen Einheiten einer eurer Truppen +1 Angriff für eine Runde");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        List<Location> locations = owner.getControlledLocations();
        Location location = selectHandler.selectLocation("Wählt eine Truppe", locations);
        IntBuff buff = new IntBuff(BuffType.ADD, 1);
        PlayerUtils.getUnits(owner, location).forEach(unit -> {
            unit.getPower().addBuff(buff);
            UnitControl.focus(game, unit, NT_Abstract_Event.EFFECT_BUFF);
        });
        game.getBuffProcessor().timeoutBuff(buff, 1);
    }

}

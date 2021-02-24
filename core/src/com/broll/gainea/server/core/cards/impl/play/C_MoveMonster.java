package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_MoveMonster extends AbstractCard {
    public C_MoveMonster() {
        super(57, "Herdentrieb", "Wählt ein Monster und bewegt es auf ein freies Feld der gleichen Karte");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        BattleObject monster = SelectionUtils.selectWildUnit(game, "Wählt ein Monster das bewegt werden soll", it -> it instanceof GodDragon == false);
        List<Area> locations = monster.getLocation().getContainer().getExpansion().getAllAreas().stream().filter(Area::isFree).collect(Collectors.toList());
        Location target = selectHandler.selectLocation("Wählt das Reiseziel", locations);
        UnitControl.move(game, monster, target);
    }

}

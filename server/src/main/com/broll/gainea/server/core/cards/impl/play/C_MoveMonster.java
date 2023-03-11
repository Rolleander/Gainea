package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

public class C_MoveMonster extends Card {
    public C_MoveMonster() {
        super(57, "Herdentrieb", "Wählt ein Monster und bewegt es auf ein beliebiges freies Feld");
    }

    @Override
    public boolean isPlayable() {
        return game.getObjects().stream().anyMatch(it -> it instanceof Monster);
    }

    @Override
    protected void play() {
        BattleObject monster = SelectionUtils.selectWildMonster(game, "Wählt ein Monster das bewegt werden soll");
        List<Area> locations = game.getMap().getAllAreas().stream().filter(Area::isFree).collect(Collectors.toList());
        Location target = selectHandler.selectLocation("Wählt das Reiseziel", locations);
        if (target != null) {
            UnitControl.move(game, monster, target);
        }
    }

}

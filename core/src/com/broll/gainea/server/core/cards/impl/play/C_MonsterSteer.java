package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;
import com.google.common.collect.Lists;

import java.util.ArrayList;

public class C_MonsterSteer extends Card {
    public C_MonsterSteer() {
        super(72, "Monsterköder", "Wählt ein Monster und bewegt es um ein Feld weiter, mögliche Ziele werden dabei angegriffen.");
    }

    @Override
    public boolean isPlayable() {
        return game.getObjects().stream().anyMatch(it -> it instanceof Monster);
    }

    @Override
    protected void play() {
        Monster monster = SelectionUtils.selectWildMonster(game,"Wähle das Monster das bewegt werden soll");
        Location target = selectHandler.selectLocation("Wähle das Ziel (Einheiten werden angegriffen)", new ArrayList<>(monster.getLocation().getWalkableNeighbours()));
        UnitControl.conquer(game, Lists.newArrayList(monster), target);
    }
}

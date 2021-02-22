package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class C_MonsterAttack extends AbstractCard {
    public C_MonsterAttack() {
        super(36, "Ogerangriff", "Wählt eine feindliche Truppe und ruft einen wilden Kriegsoger (4/4) herbei der diese angreift.");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Monster monster = new Monster();
        monster.setName("Kriegsoger");
        monster.setIcon(118);
        monster.setPower(4);
        monster.setHealth(4);
        Location target = selectHandler.selectLocation("Wählt die feindliche Truppe", new ArrayList<>(PlayerUtils.getHostileLocations(game, owner)));
        UnitControl.spawn(game, monster, target);
        game.getBattleHandler().startBattle(Lists.newArrayList(monster), PlayerUtils.getHostileArmy(owner, target));
    }


}

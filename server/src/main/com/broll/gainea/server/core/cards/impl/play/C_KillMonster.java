package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_KillMonster extends Card {
    public C_KillMonster() {
        super(6, "Rangereinsatz", "Tötet ein beliebiges Monster (Außer Götterdrache)");
    }

    @Override
    public boolean isPlayable() {
        return game.getObjects().stream().anyMatch(it -> it instanceof Monster && !(it instanceof GodDragon));
    }

    @Override
    protected void play() {
        BattleObject monster = SelectionUtils.selectWildMonster(game, "Wählt ein Monster das vernichtet werden soll", it -> it instanceof GodDragon == false);
        UnitControl.kill(game, monster);
    }

}
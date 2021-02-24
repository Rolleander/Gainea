package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class C_KillMonster extends AbstractCard {
    public C_KillMonster() {
        super(6, "Rangereinsatz", "Tötet ein beliebiges Monster auf der Karte (Außer Götterdrache)");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        BattleObject monster = SelectionUtils.selectWildUnit(game, "Wählt ein Monster das vernichtet werden soll", it -> it instanceof GodDragon == false);
        UnitControl.kill(game, monster);
    }

}

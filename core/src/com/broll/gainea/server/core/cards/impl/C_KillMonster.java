package com.broll.gainea.server.core.cards.impl;

import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.goals.AbstractGoal;
import com.broll.gainea.server.core.goals.GoalDifficulty;
import com.broll.gainea.server.core.objects.GodDragon;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.Monster;
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
        return !getMonsters().isEmpty();
    }

    @Override
    public void play() {
        List<Monster> monsters = getMonsters();
        if (!monsters.isEmpty()) {
            Monster monster = monsters.get(selectHandler.selectObject("Wählt ein Monster das vernichtet werden soll", monsters.stream().map(Monster::nt).collect(Collectors.toList())));
            UnitControl.kill(game, monster);
        }
    }

    private List<Monster> getMonsters() {
        return game.getObjects().stream().filter(it -> it instanceof Monster && it instanceof GodDragon == false).map(it -> (Monster) it).collect(Collectors.toList());
    }

}

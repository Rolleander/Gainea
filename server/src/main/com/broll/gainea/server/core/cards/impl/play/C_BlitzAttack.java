package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_BlitzAttack extends Card {
    public C_BlitzAttack() {
        super(11, "Blitzkrieg", "Wählt eine Truppe und greift damit ein beliebiges feindliches Land auf der gleichen Karte an");
        setDrawChance(0.3f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Location from = selectHandler.selectLocation("Wählt eure Angriffstruppe", owner.getControlledLocations());
        List<BattleObject> attackers = PlayerUtils.getUnits(owner, from);
        List<Location> attackLocations = PlayerUtils.getHostileLocations(game, owner).stream()
                .filter(it -> it.getContainer().getExpansion() == from.getContainer().getExpansion()).collect(Collectors.toList());
        if (!attackLocations.isEmpty()) {
            Location target = selectHandler.selectLocation("Wählt den Angriffsort aus", attackLocations);
            game.getBattleHandler().startBattle(attackers, PlayerUtils.getHostileArmy(owner, target));
        }
    }

}
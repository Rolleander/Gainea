package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;

import java.util.List;
import java.util.stream.Collectors;

public class C_HuntMonster extends Card {
    public C_HuntMonster() {
        super(37, "Drachenjagd", "Wählt eine Truppe und greift damit ein beliebiges Monster auf der gleichen Karte an");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Location from = selectHandler.selectLocation("Wählt eure Angriffstruppe", owner.getControlledLocations());
        List<BattleObject> attackers = PlayerUtils.getUnits(owner, from);
        List<Location> attackLocations = LocationUtils.getWildMonsterLocations(game).stream()
                .filter(it -> it.getContainer().getExpansion() == from.getContainer().getExpansion()).collect(Collectors.toList());
        if(!attackLocations.isEmpty()){
            Location target = selectHandler.selectLocation("Wählt den Angriffsort aus", attackLocations);
            game.getBattleHandler().startBattle(attackers, LocationUtils.getMonsters(target));
        }
    }

}

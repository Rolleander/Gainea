package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class C_MoveEnemy extends AbstractCard {
    public C_MoveEnemy() {
        super(19, "Überläufer", "Versetzt eine feindliche Truppe auf ein beliebiges freies Feld der gleichen Landmasse.");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        Location location = selectHandler.selectLocation("Armee wählen", new ArrayList<>(PlayerUtils.getHostileLocations(game, owner)));
        List<MapObject> units = location.getInhabitants().stream().collect(Collectors.toList());
        List<Area> targets = location.getContainer().getAreas().stream().filter(Area::isFree).collect(Collectors.toList());
        if (!targets.isEmpty()) {
            Location target = selectHandler.selectLocation("Zielort wählen", targets);
            UnitControl.move(game, units, target);
        }
    }

}

package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.MapObject;
import com.broll.gainea.server.core.objects.monster.GodDragon;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class C_MonsterRecruit extends Card {
    public C_MonsterRecruit() {
        super(71, "Tierfreund", "Rekrutiert ein benachbartes Monster (ausser Götterdrache)");
        setDrawChance(0.5f);
    }

    @Override
    public boolean isPlayable() {
        return !getMonsterLocations().isEmpty();
    }

    private boolean validMonster(MapObject object) {
        return object instanceof Monster && object.getOwner() == null && !(object instanceof GodDragon);
    }

    private Collection<Location> getMonsterLocations() {
        Set<Location> monsterLocations = new HashSet<>();
        owner.getControlledLocations().forEach(location ->
                location.getConnectedLocations().stream().
                        filter(it -> it.getInhabitants().stream().anyMatch(this::validMonster))
                        .forEach(monsterLocations::add)
        );
        return monsterLocations;
    }

    @Override
    protected void play() {
        Collection<Location> monsterLocations = getMonsterLocations();
        if (!monsterLocations.isEmpty()) {
            Monster monster = (Monster) SelectionUtils.selectUnitFromLocations(game,
                    new ArrayList<>(monsterLocations), this::validMonster,
                    "Welches Monster soll rekrutiert werden?");
            owner.getUnits().add(monster);
            monster.removeActionTimer(); // todo: client siehts immer noch?
            monster.setOwner(owner);
            game.getObjects().remove(monster);
            UnitControl.focus(game, monster, 0);
        }
    }
}

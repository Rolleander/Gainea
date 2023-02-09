package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.MonsterActivity;
import com.broll.gainea.server.core.objects.MonsterBehavior;
import com.broll.gainea.server.core.utils.StreamUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_ZombieSummon extends Card {
    public C_ZombieSummon() {
        super(78, "Rückkehr der Verdammten", "Ruft für die Anzahl an Kills eurer aktuellen Einheiten" +
                " jeweils einen Zombie herbei, der nicht kontrolliert werden kann.");
    }

    private int getKills() {
        return owner.getUnits().stream().mapToInt(it -> it.getKills()).sum();
    }

    @Override
    public boolean isPlayable() {
        return getKills() > 0 && !owner.getControlledLocations().isEmpty();
    }

    @Override
    protected void play() {
        StreamUtils.safeForEach(owner.getUnits().stream(), unit->{
            for (int i = 0; i < unit.getKills(); i++) {
                Zombie zombie = new Zombie();
                zombie.setOwner(owner);
                UnitControl.spawn(game, zombie, unit.getLocation());
            }
        });
    }

    private class Zombie extends Monster {

        public Zombie() {
            controllable = false;
            setName("Verdammter");
            setBehavior(MonsterBehavior.RANDOM);
            setActivity(MonsterActivity.OFTEN);
            setStats(1, 1);
            setIcon(128);
        }
    }
}

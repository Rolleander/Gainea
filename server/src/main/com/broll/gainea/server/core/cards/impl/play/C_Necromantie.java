package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.TimedEffect;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_Necromantie extends Card {

    public C_Necromantie() {
        super(70, "Nekromantie", "Für diesen Zug werden bei euren Angriffen eure gefallenen Soldaten zu Skeletten (1/1)");
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        TimedEffect.forCurrentTurn(game, new TimedEffect() {
            @Override
            public void battleResult(BattleResult result) {
                if (result.isAttacker(owner)) {
                    Location summonLocation = result.getAttackerEndLocation();
                    result.getKilledAttackers().stream().filter(it -> !(it instanceof Skeleton))
                            .forEach(it -> summonSkeleton(summonLocation));
                }
            }
        });
    }

    private void summonSkeleton(Location location) {
        UnitControl.spawn(game, new Skeleton(owner), location);
    }

    private class Skeleton extends Soldier {

        public Skeleton(Player owner) {
            super(owner);
            setIcon(94);
            setName("Skelett");
            setHealth(1);
            setPower(1);
        }
    }
}

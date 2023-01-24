package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.GlobalBuff;
import com.broll.gainea.server.core.objects.buffs.TimedEffect;
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
        //todo geht nicht ?
        TimedEffect.forCurrentTurn(game, new TimedEffect() {
            @Override
            public void battleResult(BattleResult result) {
                if (result.getAttacker() == owner) {
                    Location summonLocation = result.attackersWon() ? result.getLocation() : result.getAttackerSourceLocation();
                    result.getKilledAttackers().forEach(it -> summonSkeleton(summonLocation));
                }
            }
        });
    }

    private void summonSkeleton(Location location) {
        Soldier skeleton = new Soldier(owner);
        skeleton.setIcon(94);
        skeleton.setName("Skelett");
        skeleton.setHealth(1);
        skeleton.setPower(1);
        UnitControl.spawn(game, skeleton, location);
    }
}

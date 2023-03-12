package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.buffs.TimedEffect;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_Prisoners extends Card {

    public C_Prisoners() {
        super(20, "Kriegsgefangene", "Rekrutiert alle besiegte feindliche Soldaten beim nächsten siegreichen Kampf in diesem Zug (Ausser Feldherren)");
        setDrawChance(0.6f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        TimedEffect.forCurrentTurn(game, new TimedEffect() {
            public void battleResult(BattleResult result) {
                if (result.isWinner(owner)) {
                    result.getOpposingUnits(owner).stream()
                            .filter(it -> it instanceof Soldier && it.isDead() && !PlayerUtils.isCommander(it))
                            .forEach(unit -> recruit(unit, result.getEndLocation(owner)));
                    unregister();
                }
            }

            private void recruit(Unit unit, Location location) {
                unit.heal();
                //so they cant be used this turn
                unit.attacked();
                unit.moved();
                unit.setOwner(owner);
                UnitControl.spawn(game, unit, location);
            }
        });
    }

}

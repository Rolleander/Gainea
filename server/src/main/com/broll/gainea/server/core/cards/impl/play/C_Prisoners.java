package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.objects.buffs.TimedEffect;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;
import java.util.stream.Collectors;

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
                    List<Unit> killedSoldiers = result.getOpposingUnits(owner).stream()
                            .filter(it -> it instanceof Soldier && it.isDead() && !PlayerUtils.isCommander(it))
                            .collect(Collectors.toList());
                    UnitControl.recruit(game, owner, killedSoldiers, result.getEndLocation(owner));
                    unregister();
                }
            }
        });
    }

}

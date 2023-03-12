package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.RollManipulator;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.buffs.TimedEffect;

public class C_BattleDebuff extends Card {

    public C_BattleDebuff() {
        super(53, "Pfeilhagel", "-1 Zahl für die feindliche Armee bei eurem nächsten Kampf diesen Zug.");
        setDrawChance(0.6f);
    }

    @Override
    public boolean isPlayable() {
        return true;
    }

    @Override
    protected void play() {
        TimedEffect.forCurrentTurn(game, new TimedEffect() {
            @Override
            public void battleBegin(BattleContext context, RollManipulator rollManipulator) {
                rollManipulator.register((attackerRolls, defenderRolls) -> {
                    if (context.isAttacker(owner)) {
                        defenderRolls.plusNumber(-1);
                    } else {
                        attackerRolls.plusNumber(-1);
                    }
                });
                unregister();
            }
        });
    }
}

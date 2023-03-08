package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.buffs.TimedEffect;

public class C_Prisoners extends Card {
    private final static int COUNT = 3;

    public C_Prisoners() {
        super(20, "Kriegsgefangene", "Nach Aktivierung erhaltet ihr " + COUNT + " Soldaten, sobald ihr in diesem Zug einen Angriff gegen Soldaten gewinnt.");
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
                if (result.isAttacker(owner) && result.attackersWon()) {
                    if (result.getDefenders().stream().anyMatch(it -> it instanceof Soldier)) {
                        for (int i = 0; i < COUNT; i++) {
                            placeUnitHandler.placeSoldier(owner);
                        }
                        game.getUpdateReceiver().unregister(this);
                    }
                }
            }
        });
    }

}

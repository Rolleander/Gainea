package com.broll.gainea.server.core.cards.impl.play;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Monster;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;
import com.google.common.collect.Lists;

import java.util.ArrayList;

public class C_Prisoners extends AbstractCard {
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
        game.getUpdateReceiver().register(new GameUpdateReceiverAdapter() {
            @Override
            public void battleResult(BattleResult result) {
                if (result.getAttacker() == owner && result.attackersWon()) {
                    if (result.getDefenders().stream().anyMatch(it -> it instanceof Soldier)) {
                        for (int i = 0; i < COUNT; i++) {
                            placeUnitHandler.placeSoldier(owner);
                        }
                        game.getUpdateReceiver().unregister(this);
                    }
                }
            }

            @Override
            public void turnStarted(Player player) {
                game.getUpdateReceiver().unregister(this);
            }
        });
    }

}

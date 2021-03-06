package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class C_UnknownSoldier extends DirectlyPlayedCard {

    public C_UnknownSoldier() {
        super(67, "Mytseriöser Herausforderer", "Ein fremder Herausforderer taucht auf! Wer ihn besiegt erhält einen Siegespunkt.");
        setDrawChance(0.2f);
    }

    @Override
    protected void play() {
        Location location = LocationUtils.getRandomFree(game.getMap().getAllAreas());
        if (location != null) {
            Challenger soldier = new Challenger();
            UnitControl.spawn(game, soldier, location);
            game.getUpdateReceiver().register(new GameUpdateReceiverAdapter() {
                @Override
                public void killed(BattleObject unit, BattleResult throughBattle) {
                    if(unit == soldier){
                        if(throughBattle!=null){
                            throughBattle.getWinnerPlayer().getGoalHandler().addPoints(1);
                        }
                        game.getUpdateReceiver().unregister(this);
                    }
                }
            });
        }
    }

    private class Challenger extends Soldier {

        public Challenger() {
            super(null);
            setName("Der Fremde");
            setIcon(126);
            setStats(7, 7);
        }

    }
}

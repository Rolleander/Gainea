package com.broll.gainea.server.core.cards.impl.direct;

import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.cards.DirectlyPlayedCard;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Soldier;
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
        }
    }

    private class Challenger extends Soldier {

        public Challenger() {
            super(null);
            setName("Der Fremde");
            setIcon(126);
            setStats(7, 7);
        }

        @Override
        public void onDeath(BattleResult throughBattle) {
            if (throughBattle != null) {
                throughBattle.getKillingPlayers(this).forEach(p -> p.getGoalHandler().addPoints(1));
            }
        }

    }
}

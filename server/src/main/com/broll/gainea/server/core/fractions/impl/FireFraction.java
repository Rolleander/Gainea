package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.cards.Card;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.monster.Monster;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.SelectionUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class FireFraction extends Fraction {

    private int turns;

    public FireFraction() {
        super(FractionType.FIRE);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Erhält jede dritte Runde eine Feuerregen-Karte\n(Verursacht 1 Schaden an einer beliebigen feindlichen Einheit)");
        desc.contra("Erhält keine Belohnung für besiegte Monster auf Schnee oder Seen");
        return desc;
    }

    @Override
    public void prepareTurn(ActionHandlers actionHandlers) {
        turns++;
        if (turns == 3) {
            owner.getCardHandler().receiveCard(new FireRain());
            turns = 0;
        }
        super.prepareTurn(actionHandlers);
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Feuermagier");
        soldier.setIcon(23);
        return soldier;
    }


    @Override
    public Commander createCommander() {
        Commander commander = new Commander(owner);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Flammenschürer Duras");
        commander.setIcon(48);
        return commander;
    }

    @Override
    public void killedMonster(Monster monster) {
        if (LocationUtils.isAreaType(monster.getLocation(), AreaType.SNOW, AreaType.LAKE)) {
            //no card when monster on ice or water
            return;
        }
        super.killedMonster(monster);
    }

    private class FireRain extends Card {

        public FireRain() {
            super(76, "Feuerregen", "Verursacht 1 Schaden an einer beliebigen feindlichen Einheit");
        }

        @Override
        public boolean isPlayable() {
            return true;
        }

        @Override
        protected void play() {
            BattleObject unit = SelectionUtils.selectEnemyUnit(game, owner, "Welcher Einheit soll Schaden zugeführt werden?");
            if (unit != null) {
                UnitControl.damage(game, unit, 1);
            }
        }
    }
}


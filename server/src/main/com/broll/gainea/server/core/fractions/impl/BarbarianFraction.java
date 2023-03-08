package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class BarbarianFraction extends Fraction {
    private static final int SUMMON_TURN = 3;
    private int turns;
    private BarbarianBrother brother;

    public BarbarianFraction() {
        super(FractionType.BARBARIANS);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Nach " + SUMMON_TURN + " Runden ruft der Kommandant seine zweite Hand (2/3) herbei");
        desc.contra("Im Sumpf -1 Würfel");
        return desc;
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        if (area.getType() == AreaType.BOG) {
            power.changeDiceNumber(-1);
        }
    }

    @Override
    public Soldier createSoldier() {
        Soldier soldier = new Soldier(owner);
        soldier.setStats(SOLDIER_POWER, SOLDIER_HEALTH);
        soldier.setName("Barbarenrkieger");
        soldier.setIcon(103);
        return soldier;
    }


    @Override
    public Commander createCommander() {
        Commander commander = new Commander(owner);
        commander.setStats(COMMANDER_POWER, COMMANDER_HEALTH);
        commander.setName("Barbarenanführer");
        commander.setIcon(45);
        return commander;
    }

    @Override
    public void prepareTurn(ActionHandlers actionHandlers) {
        super.prepareTurn(actionHandlers);
        if (turns == SUMMON_TURN) {
            if (brother == null || brother.isDead()) {
                summon();
            }
        } else {
            turns++;
        }
    }

    private void summon() {
        PlayerUtils.getCommander(owner).ifPresent(commander -> {
            brother = new BarbarianBrother(owner);
            UnitControl.spawn(game, brother, commander.getLocation());
        });
    }

    private class BarbarianBrother extends Soldier {

        public BarbarianBrother(Player owner) {
            super(owner);
            setStats(2, 3);
            setName("Zweite Hand");
            setIcon(49);
        }

        @Override
        public void onDeath(BattleResult throughBattle) {
            turns = 0;
        }
    }
}

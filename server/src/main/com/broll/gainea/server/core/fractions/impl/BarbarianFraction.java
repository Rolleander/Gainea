package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.objects.Unit;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;

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
        desc.plus("+1 Zahl, wenn Kommandant und zweite Hand zusammen kämpfen");
        desc.contra("-1 Zahl, wenn keine Barbarenrkieger im Kampf beteiligt sind");
        return desc;
    }


    @Override
    public FightingPower calcFightingPower(Soldier soldier, BattleContext context) {
        FightingPower power = super.calcFightingPower(soldier, context);
        List<Unit> army = context.getFightingArmy(soldier);
        if (army.stream().noneMatch(this::isPlainBarbarianSoldier)) {
            power.changeNumberPlus(-1);
        }
        if (army.stream().anyMatch(it -> it instanceof Commander) && army.stream().anyMatch(it -> it instanceof BarbarianBrother)) {
            power.changeNumberPlus(1);
        }
        return power;
    }

    private boolean isPlainBarbarianSoldier(Unit unit) {
        return unit instanceof Soldier && !(unit instanceof Commander) &&
                !(unit instanceof BarbarianBrother) && ((Soldier) unit).getFraction() == this;
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

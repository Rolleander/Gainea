package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.server.core.actions.ActionHandlers;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.PlayerUtils;
import com.broll.gainea.server.core.utils.UnitControl;

public class BarbarianFraction extends Fraction {
    private static int SUMMON_TURN = 5;
    private int turns;
    private BarbarianBrother brother;

    public BarbarianFraction() {
        super(FractionType.BARBARIANS);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Nach "+SUMMON_TURN+" Runden ruft der Kommandant seine zweite Hand (3/3) herbei");
        desc.contra("Im Sumpf -1 Würfel");
        return desc;
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        if(LocationUtils.isAreaType(area, AreaType.BOG)){
            power.changeDiceNumber(-1);
        }
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Barbarenrkieger");
        soldier.setIcon(103);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Barbarenanführer");
        commander.setIcon(45);
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
            brother.init(game);
            owner.getUnits().add(brother);
            UnitControl.spawn(game, brother, commander.getLocation());
        });
    }

    private class BarbarianBrother extends Soldier {

        public BarbarianBrother(Player owner) {
            super(owner);
            setStats(3, 3);
            setName("Zweite Hand");
            setIcon(49);
        }

        @Override
        protected void onDeath() {
            //summon again after N turns
            turns = 0;
        }
    }
}

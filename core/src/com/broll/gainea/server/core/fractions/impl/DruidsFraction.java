package com.broll.gainea.server.core.fractions.impl;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.Ship;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;

public class DruidsFraction extends Fraction {

    private static float SPAWN_CHANCE = 0.4f;

    public DruidsFraction() {
        super(FractionType.DRUIDS);
    }

    @Override
    public void init(GameContainer game, Player owner) {
        super.init(game, owner);
        game.getUpdateReceiver().register(new GameUpdateReceiverAdapter() {
            @Override
            public void battleResult(BattleResult result) {
                result.getAttackers().stream().filter(it -> it.getOwner() == owner && it.isDead()).forEach(DruidsFraction.this::druidDied);
                result.getDefenders().stream().filter(it -> it.getOwner() == owner && it.isDead()).forEach(DruidsFraction.this::druidDied);
            }

            @Override
            public void damaged(BattleObject unit, int damage) {
                if (unit.isDead() && unit.getOwner() == owner) {
                    druidDied(unit);
                }
            }
        });
    }

    private void druidDied(BattleObject unit) {
        if (unit instanceof Tree == false && MathUtils.randomBoolean(SPAWN_CHANCE)) {
            Tree tree = new Tree(owner);
            tree.init(game);
            UnitControl.spawn(game, tree, unit.getLocation());
        }
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Druidin");
        soldier.setIcon(110);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Druidenhaupt Zerus");
        commander.setIcon(102);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Gefallene Einheiten können zu unbeweglichen Wurzelgolems (2/2) werden");
        desc.contra("Auf Schiffen -1 Zahl");
        return desc;
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power= super.calcPower(location, fighters, enemies, isAttacker);
        if(location instanceof Ship){
            power.changeNumberPlus(-1);
        }
        return power;
    }

    private class Tree extends Soldier {
        public Tree(Player owner) {
            super(owner);
            setStats(2, 2);
            setName("Wurzelgolem");
            setIcon(99);
            rooted = true;
        }
    }
}

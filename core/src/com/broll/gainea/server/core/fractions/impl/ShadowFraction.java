package com.broll.gainea.server.core.fractions.impl;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.Area;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.LocationUtils;
import com.broll.gainea.server.core.utils.UnitControl;

import java.util.List;

public class ShadowFraction extends Fraction {
    private static float SUMMON_CHANCE = 0.3f;

    public ShadowFraction() {
        super(FractionType.SHADOW);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        desc.plus("Bei siegreichen Kämpfen können gefallene Feinde zu Skeletten (1/1) werden");
        desc.contra("Auf Grasland -2 Würfel");
        return desc;
    }

    @Override
    protected void powerMutatorArea(FightingPower power, Area area) {
        if (area.getType() == AreaType.PLAINS) {
            power.changeDiceNumber(-2);
        }
    }

    @Override
    protected void initSoldier(Soldier soldier) {
        soldier.setName("Schatten");
        soldier.setIcon(12);
    }

    @Override
    protected void initCommander(Commander commander) {
        commander.setName("Erznekromant Bal");
        commander.setIcon(21);
    }

    private void afterAttack(List<BattleObject> enemies, Location location) {
        enemies.stream().filter(BattleObject::isDead).forEach(it -> {
            if (MathUtils.randomBoolean(SUMMON_CHANCE)) {
                summon(location);
            }
        });
    }

    private void summon(Location location) {
        Soldier skeleton = createSoldier();
        skeleton.setIcon(94);
        skeleton.setName("Skelett");
        UnitControl.spawn(game, skeleton, location);
    }

    @Override
    public void battleResult(BattleResult result) {
        if (result.getWinnerPlayer() == ShadowFraction.this.owner) {
            afterAttack(result.getLoserUnits(), result.getLocation());
        }
    }

}

package com.broll.gainea.server.core.fractions.impl;

import com.badlogic.gdx.math.MathUtils;
import com.broll.gainea.client.ui.elements.LabelUtils;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.battle.BattleResult;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.cards.AbstractCard;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.AreaType;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.objects.Commander;
import com.broll.gainea.server.core.objects.Soldier;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.utils.GameUpdateReceiverAdapter;
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
        desc.contra("Auf Grasland -2 Würfel");
        desc.plus("Bei gewonnen Kämpfen können gefallene Feinde zu Skeletten (1/1) werden");
        return desc;
    }

    @Override
    public FightingPower calcPower(Location location, List<BattleObject> fighters, List<BattleObject> enemies, boolean isAttacker) {
        FightingPower power = super.calcPower(location, fighters, enemies, isAttacker);
        if(LocationUtils.isAreaType(location, AreaType.PLAINS)){
            power.changeDiceNumber(-2);
        }
        return power;
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

    private void afterAttack(List<BattleObject> defenders, Location location) {
        defenders.stream().filter(BattleObject::isDead).forEach(it -> {
            if (MathUtils.randomBoolean(SUMMON_CHANCE)) {
                summon(location);
            }
        });
    }

    private void summon(Location location) {
        Soldier skeleton = createSoldier(location);
        skeleton.setIcon(94);
        skeleton.setName("Skelett");
        owner.getUnits().add(skeleton);
        UnitControl.spawn(game, skeleton, location);
    }

    @Override
    public void init(GameContainer game, Player owner) {
        super.init(game, owner);
        game.getUpdateReceiver().register(new GameUpdateReceiverAdapter() {
            @Override
            public void battleResult(BattleResult result) {
                if (result.getAttacker() == ShadowFraction.this.owner && result.attackersWon()) {
                    afterAttack(result.getDefenders(), result.getLocation());
                }
            }
        });
    }
}
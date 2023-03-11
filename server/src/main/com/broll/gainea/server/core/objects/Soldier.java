package com.broll.gainea.server.core.objects;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.player.Player;

public class Soldier extends Unit {

    private Fraction fraction;

    public Soldier(Player owner) {
        super(owner);
        this.fraction = owner.getFraction();
        setIcon(4);
    }

    @Override
    public FightingPower calcFightingPower(BattleContext context) {
        return fraction.calcFightingPower(this, context);
    }

    public Fraction getFraction() {
        return fraction;
    }
}

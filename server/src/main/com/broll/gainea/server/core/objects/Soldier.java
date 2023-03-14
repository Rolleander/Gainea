package com.broll.gainea.server.core.objects;

import com.broll.gainea.server.core.battle.BattleContext;
import com.broll.gainea.server.core.battle.FightingPower;
import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.player.Player;

public class Soldier extends Unit {

    private Fraction fraction;
    private boolean commander;

    public Soldier(Player owner) {
        super(owner);
        if (owner != null) {
            this.fraction = owner.getFraction();
        }
    }

    public void setCommander(boolean commander) {
        this.commander = commander;
    }

    public boolean isCommander() {
        return commander;
    }

    @Override
    public FightingPower calcFightingPower(BattleContext context) {
        return fraction.calcFightingPower(this, context);
    }

    public Fraction getFraction() {
        return fraction;
    }
}

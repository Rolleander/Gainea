package com.broll.gainea.server.core.fractions.impl;

import com.broll.gainea.server.core.fractions.Fraction;
import com.broll.gainea.server.core.fractions.FractionDescription;
import com.broll.gainea.server.core.fractions.FractionType;

public class WaterFraction extends Fraction {

    public WaterFraction() {
        super(FractionType.WATER);
    }

    @Override
    protected FractionDescription description() {
        FractionDescription desc = new FractionDescription("");
        return desc;
    }

}

package com.broll.gainea.server.core.fractions;

import com.broll.gainea.server.core.fractions.impl.BarbarianFraction;
import com.broll.gainea.server.core.fractions.impl.DruidsFraction;
import com.broll.gainea.server.core.fractions.impl.FireFraction;
import com.broll.gainea.server.core.fractions.impl.GuardsFraction;
import com.broll.gainea.server.core.fractions.impl.KnightsFraction;
import com.broll.gainea.server.core.fractions.impl.LizardFraction;
import com.broll.gainea.server.core.fractions.impl.MercenaryFraction;
import com.broll.gainea.server.core.fractions.impl.MonkFraction;
import com.broll.gainea.server.core.fractions.impl.PoacherFraction;
import com.broll.gainea.server.core.fractions.impl.RangerFraction;
import com.broll.gainea.server.core.fractions.impl.SamuraiFraction;
import com.broll.gainea.server.core.fractions.impl.ShadowFraction;
import com.broll.gainea.server.core.fractions.impl.VikingFraction;
import com.broll.gainea.server.core.fractions.impl.WaterFraction;

import java.util.function.Supplier;

public enum FractionType {

    SHADOW("Schatten", ShadowFraction::new),
    BARBARIANS("Barbaren", BarbarianFraction::new),
    VIKINGS("Wikinger", VikingFraction::new),
    DRUIDS("Druiden", DruidsFraction::new),
    KNIGHTS("Kreuzritter", KnightsFraction::new),
    SAMURAI("Samurai", SamuraiFraction::new),
    MONKS("Mönche", MonkFraction::new),
    POACHER("Wilderer", PoacherFraction::new),
    RANGER("Waldläufer", RangerFraction::new),
    MERCENARY("Söldner", MercenaryFraction::new),
    GUARDS("Königsgarde", GuardsFraction::new),
    FIRE("Feuermagier", FireFraction::new),
    WATER("Wassermagier", WaterFraction::new),
    LIZARDS("Echsenvolk", LizardFraction::new);

    private String name;
    private Supplier<Fraction> factory;

    FractionType(String name, Supplier<Fraction> factory) {
        this.name = name; this.factory = factory;
    }

    public Fraction create(){
        return factory.get();
    }

    public String getName() {
        return name;
    }
}

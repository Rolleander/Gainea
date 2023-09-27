package com.broll.gainea.server.core.fractions

import com.broll.gainea.server.core.fractions.impl.BarbarianFraction
import com.broll.gainea.server.core.fractions.impl.DruidsFraction
import com.broll.gainea.server.core.fractions.impl.LizardFraction
import com.broll.gainea.server.core.fractions.impl.MercenaryFraction
import com.broll.gainea.server.core.fractions.impl.ShadowFraction
import com.broll.gainea.server.core.fractions.impl.WaterFraction
import java.util.function.Supplier

enum class FractionType(override val name: String, private val factory: Supplier<Fraction>) {
    SHADOW("Schatten", Supplier<Fraction> { ShadowFraction() }),
    BARBARIANS("Barbaren", Supplier<Fraction> { BarbarianFraction() }),
    VIKINGS("Wikinger", Supplier<Fraction> { VikingFraction() }),
    DRUIDS("Druiden", Supplier<Fraction> { DruidsFraction() }),
    KNIGHTS("Kreuzritter", Supplier<Fraction> { KnightsFraction() }),
    SAMURAI("Samurai", Supplier<Fraction> { SamuraiFraction() }),
    MONKS("Mönche", Supplier<Fraction> { MonkFraction() }),
    POACHER("Wilderer", Supplier<Fraction> { PoacherFraction() }),
    RANGER("Waldläufer", Supplier<Fraction> { RangerFraction() }),
    MERCENARY("Söldner", Supplier<Fraction> { MercenaryFraction() }),
    GUARDS("Königsgarde", Supplier<Fraction> { GuardsFraction() }),
    FIRE("Feuermagier", Supplier<Fraction> { FireFraction() }),
    WATER("Wassermagier", Supplier<Fraction> { WaterFraction() }),
    LIZARDS("Echsenvolk", Supplier<Fraction> { LizardFraction() });

    fun create(): Fraction {
        return factory.get()
    }
}

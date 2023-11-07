package com.broll.gainea.server.core.fractions

import com.broll.gainea.server.core.fractions.impl.BarbarianFraction
import com.broll.gainea.server.core.fractions.impl.DruidsFraction
import com.broll.gainea.server.core.fractions.impl.FireFraction
import com.broll.gainea.server.core.fractions.impl.GuardsFraction
import com.broll.gainea.server.core.fractions.impl.KnightsFraction
import com.broll.gainea.server.core.fractions.impl.LizardFraction
import com.broll.gainea.server.core.fractions.impl.MercenaryFraction
import com.broll.gainea.server.core.fractions.impl.MonkFraction
import com.broll.gainea.server.core.fractions.impl.PoacherFraction
import com.broll.gainea.server.core.fractions.impl.RangerFraction
import com.broll.gainea.server.core.fractions.impl.SamuraiFraction
import com.broll.gainea.server.core.fractions.impl.ShadowFraction
import com.broll.gainea.server.core.fractions.impl.SpartanFraction
import com.broll.gainea.server.core.fractions.impl.VikingFraction
import com.broll.gainea.server.core.fractions.impl.WaterFraction


enum class FractionType(val displayName: String, private val factory: () -> Fraction) {
    SHADOW("Schatten", { ShadowFraction() }),
    BARBARIANS("Barbaren", { BarbarianFraction() }),
    VIKINGS("Wikinger", { VikingFraction() }),
    DRUIDS("Druiden", { DruidsFraction() }),
    KNIGHTS("Kreuzritter", { KnightsFraction() }),
    SAMURAI("Samurai", { SamuraiFraction() }),
    MONKS("Mönche", { MonkFraction() }),
    POACHER("Wilderer", { PoacherFraction() }),
    RANGER("Waldläufer", { RangerFraction() }),
    MERCENARY("Söldner", { MercenaryFraction() }),
    GUARDS("Königsgarde", { GuardsFraction() }),
    FIRE("Feuermagier", { FireFraction() }),
    WATER("Wassermagier", { WaterFraction() }),
    SPARTANS("Spartaner", { SpartanFraction() }),
    LIZARDS("Echsenvolk", { LizardFraction() });

    fun create(): Fraction {
        return factory()
    }
}

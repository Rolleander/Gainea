package com.broll.gainea.server.core.fractions

import com.broll.gainea.server.core.fractions.impl.*

fun FractionType.create(): Fraction = when (this) {
    FractionType.SHADOW -> ShadowFraction()
    FractionType.BARBARIANS -> BarbarianFraction()
    FractionType.VIKINGS -> VikingFraction()
    FractionType.DRUIDS -> DruidsFraction()
    FractionType.KNIGHTS -> KnightsFraction()
    FractionType.SAMURAI -> SamuraiFraction()
    FractionType.MONKS -> MonkFraction()
    FractionType.POACHER -> PoacherFraction()
    FractionType.RANGER -> RangerFraction()
    FractionType.MERCENARY -> MercenaryFraction()
    FractionType.GUARDS -> GuardsFraction()
    FractionType.FIRE -> FireFraction()
    FractionType.WATER -> WaterFraction()
    FractionType.SPARTANS -> SpartanFraction()
    FractionType.LIZARDS -> LizardFraction()
}

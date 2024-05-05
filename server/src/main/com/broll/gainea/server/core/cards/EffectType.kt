package com.broll.gainea.server.core.cards

enum class EffectType(val displayName: String) {
    BUFF("Buff"),
    DEBUFF("Debuff"),
    MOVEMENT("Transportation"),
    DISRUPTION("Manipulation"),
    CHAOS("Chaos"),
    SUMMON("Beschwörung"),
    OTHER("Andere")
}
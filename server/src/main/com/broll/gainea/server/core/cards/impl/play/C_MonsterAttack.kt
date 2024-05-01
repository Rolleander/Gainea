package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.EffectType.DISRUPTION
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterActivity
import com.broll.gainea.server.core.objects.monster.MonsterBehavior
import com.broll.gainea.server.core.utils.UnitControl.spawn
import com.broll.gainea.server.core.utils.getEnemyLocations
import com.broll.gainea.server.core.utils.getHostileArmy
import com.google.common.collect.Lists

class C_MonsterAttack : Card(
    36,
    DISRUPTION,
    "Ogerangriff",
    "Wählt eine feindliche Truppe und ruft einen wilden Kriegsoger (4/4) herbei der diese angreift."
) {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val monster = Monster(game.neutralPlayer)
        monster.name = "Kriegsoger"
        monster.icon = 118
        monster.setPower(4)
        monster.setHealth(4)
        monster.behavior = MonsterBehavior.RANDOM
        monster.activity = MonsterActivity.SOMETIMES
        val target = selectHandler.selectLocation(
            "Wählt die feindliche Truppe",
            game.getEnemyLocations(owner).toList()
        )
        val hostileArmy = owner.getHostileArmy(target)
        game.spawn(monster, target)
        game.battleHandler.startBattle(Lists.newArrayList(monster), hostileArmy)
    }
}

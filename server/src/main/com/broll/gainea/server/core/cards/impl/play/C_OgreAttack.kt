package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterActivity
import com.broll.gainea.server.core.objects.monster.MonsterBehavior
import com.broll.gainea.server.core.utils.PlayerUtils
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_OgreAttack : Card(36, "Ogerangriff", "Wählt eine feindliche Truppe und ruft einen wilden Kriegsoger (4/4) herbei der diese angreift.") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val monster = Monster()
        monster.name = "Kriegsoger"
        monster.icon = 118
        monster.setPower(4)
        monster.setHealth(4)
        monster.setBehavior(MonsterBehavior.RANDOM)
        monster.setActivity(MonsterActivity.SOMETIMES)
        val target = selectHandler.selectLocation("Wählt die feindliche Truppe", PlayerUtils.getHostileLocations(game, owner).toList())
        val hostileArmy = PlayerUtils.getHostileArmy(owner, target)
        spawn(game, monster, target)
        game.battleHandler.startBattle(listOf(monster), hostileArmy)
    }
}

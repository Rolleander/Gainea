package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.objects.monster.MonsterActivityimport com.broll.gainea.server.core.objects.monster.MonsterBehaviorimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.google.common.collect.Lists
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
        val target = selectHandler!!.selectLocation("Wählt die feindliche Truppe", ArrayList(PlayerUtils.getHostileLocations(game!!, owner)))
        val hostileArmy = PlayerUtils.getHostileArmy(owner, target)
        spawn(game, monster, target)
        game.battleHandler.startBattle(Lists.newArrayList(monster), hostileArmy)
    }
}

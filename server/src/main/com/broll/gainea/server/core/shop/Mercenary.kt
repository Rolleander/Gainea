package com.broll.gainea.server.core.shop

import com.broll.gainea.net.NT_Event
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.UnitControl.focus

open class Mercenary(
    owner: Player,
    stats: Pair<Int, Int> = 1 to 1,
    icon: Int,
    name: String,
    description: String? = null
) : Soldier(owner) {
    init {
        setStats(stats.first, stats.second)
        this.icon = icon
        this.name = name
        this.description = description
    }
}

class SoulStealer(owner: Player) : Mercenary(owner, 4 to 3, 18, "Seelensammler") {

    init {
        description = "Erhält nach Kämpfen +1 Leben für jeden getöteten Feind"
    }

    override fun battleResult(result: BattleResult) {
        if (!result.isParticipating(this)) return
        val addHealth = result.rounds.flatMap { it.damageTaken }.count {
            it.source.owner == owner && it.lethalHit
        }
        if (addHealth > 0) {
            this.addHealth(addHealth)
            game.focus(this, NT_Event.EFFECT_BUFF)
        }
    }

}
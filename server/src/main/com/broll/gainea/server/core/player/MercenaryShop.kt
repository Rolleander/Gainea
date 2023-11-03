package com.broll.gainea.server.core.player

import com.broll.gainea.net.NT_Event
import com.broll.gainea.net.NT_MercShop
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.objects.impl.Challenger
import com.broll.gainea.server.core.objects.impl.DemonKnight
import com.broll.gainea.server.core.utils.UnitControl.focus
import com.broll.gainea.server.core.utils.isCommanderAlive

class MercenaryShop(val game: Game, val owner: Player) {

    val units: List<ShopUnit>
        get() = listOf(
            ShopUnit(4, Mercenary(owner, 1 to 1, 10, "Leichter Söldner")),
            ShopUnit(7, Mercenary(owner, 2 to 2, 28, "Schwerer Söldner")),
            ShopUnit(8, DemonKnight(owner)),
            ShopUnit(10, owner.fraction.createCommander(), !owner.isCommanderAlive()),
            ShopUnit(13, SoulStealer(owner)),
            ShopUnit(20, Challenger(owner)),
        )

    fun nt(): NT_MercShop {
        val nt = NT_MercShop()
        nt.prices = units.map { it.price.toShort() }.toShortArray()
        nt.units = units.map { it.unit.nt() }.toTypedArray()
        return nt
    }

}

data class ShopUnit(val price: Int, val unit: Unit, val available: Boolean = true)
private open class Mercenary(
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

private class SoulStealer(owner: Player) : Mercenary(owner, 4 to 3, 18, "Seelensammler") {

    init {
        description = "Erhält nach Kämpfen +1 Leben für jeden getöteten Feind"
    }

    override fun battleResult(result: BattleResult) {
        val addHealth = result.rounds.flatMap { it.damageTaken }.count {
            it.source.owner == owner && it.lethalHit
        }
        if (addHealth > 0) {
            this.addHealth(addHealth)
            game.focus(this, NT_Event.EFFECT_BUFF)
        }
    }

}
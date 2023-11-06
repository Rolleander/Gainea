package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.battle.BattleResult
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Soldier
import com.broll.gainea.server.core.objects.buffs.TimedEffect
import com.broll.gainea.server.core.player.Player
import com.broll.gainea.server.core.utils.UnitControl.spawn

class C_Necromanty : Card(
    70,
    "Nekromantie",
    "Für diesen Zug werden bei euren Angriffen eure gefallenen Soldaten zu Skeletten (1/1)"
) {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        TimedEffect.forCurrentTurn(game, object : TimedEffect() {
            override fun battleResult(result: BattleResult) {
                if (result.isAttacker(owner)) {
                    val summonLocation = result.attackerEndLocation
                    result.killedAttackers.map { it.source }.filter { it !is Skeleton }
                        .forEach { _ -> summonSkeleton(summonLocation) }
                }
            }
        })
    }

    private fun summonSkeleton(location: Location) {
        game.spawn(Skeleton(owner), location)
    }

    private class Skeleton(owner: Player) : Soldier(owner) {
        init {
            icon = 94
            name = "Skelett"
            numberPlus.value = -1
            setHealth(1)
            setPower(1)
        }
    }
}

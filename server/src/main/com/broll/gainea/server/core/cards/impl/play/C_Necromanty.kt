package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.battle.BattleResultimport

com.broll.gainea.server.core.objects.Soldierimport com.broll.gainea.server.core.objects.buffs.TimedEffect com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.player.Player
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListener

class C_Necromanty : Card(70, "Nekromantie", "Für diesen Zug werden bei euren Angriffen eure gefallenen Soldaten zu Skeletten (1/1)") {
    override val isPlayable: Boolean
        get() = true

    override fun play() {
        TimedEffect.Companion.forCurrentTurn(game!!, object : TimedEffect() {
            override fun battleResult(result: BattleResult) {
                if (result.isAttacker(owner)) {
                    val summonLocation = result.attackerEndLocation
                    result.killedAttackers.stream().filter { it: Unit? -> it !is Skeleton }
                            .forEach { it: Unit? -> summonSkeleton(summonLocation) }
                }
            }
        })
    }

    private fun summonSkeleton(location: Location?) {
        spawn(game, Skeleton(owner), location)
    }

    private class Skeleton(owner: Player?) : Soldier(owner) {
        init {
            icon = 94
            name = "Skelett"
            setHealth(1)
            setPower(1)
        }
    }
}

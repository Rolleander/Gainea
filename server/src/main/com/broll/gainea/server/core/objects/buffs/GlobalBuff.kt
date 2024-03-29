package com.broll.gainea.server.core.objects.buffs

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import java.util.function.Consumer

class GlobalBuff(
        val targets: List<Player>,
        val buff: Buff<*>,
        private val applier: Consumer<Unit>
) {

    fun apply(`object`: Unit) {
        applier.accept(`object`)
    }

    companion object {
        fun createForPlayer(game: Game, target: Player, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(listOf(target), buff, applier), effect)
        }

        fun createForAllPlayers(game: Game, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(ArrayList(game.allPlayers), buff, applier), effect)
        }

        fun createForPlayers(game: Game, targets: List<Player>, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(targets, buff, applier), effect)
        }

        fun createForNeutral(game: Game, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(listOf(game.neutralPlayer), buff, applier), effect)
        }

        fun createForAll(game: Game, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(listOf(game.allPlayers, listOf(game.neutralPlayer)).flatten(), buff, applier), effect)
        }

        private fun register(game: Game, buff: GlobalBuff, effect: Int) {
            game.buffProcessor.addGlobalBuff(buff, effect)
        }
    }
}

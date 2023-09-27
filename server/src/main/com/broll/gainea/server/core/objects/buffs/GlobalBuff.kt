package com.broll.gainea.server.core.objects.buffs

import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.objects.Unit
import com.broll.gainea.server.core.player.Player
import com.google.common.collect.Lists
import java.util.function.Consumer

class GlobalBuff(
        val forNeutral : Boolean ,
        val targets: List<Player>,
        val buff: Buff<*>,
        private val applier: Consumer<Unit>
)  {

    fun apply(`object`: Unit) {
        applier.accept(`object`)
    }

    companion object {
        fun createForPlayer(game: GameContainer, target: Player, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(false, listOf(target), buff, applier), effect)
        }

        fun createForAllPlayers(game: GameContainer, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(false,ArrayList(game.allPlayers), buff, applier), effect)
        }

        fun createForPlayers(game: GameContainer, targets: List<Player>, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(false,targets, buff, applier), effect)
        }

        fun createForNeutral(game: GameContainer, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(true,listOf(), buff, applier), effect)
        }

        fun createForAll(game: GameContainer, buff: Buff<*>, applier: Consumer<Unit>, effect: Int) {
            register(game, GlobalBuff(true, game.allPlayers, buff, applier), effect)
        }

        private fun register(game: GameContainer, buff: GlobalBuff, effect: Int) {
            game.buffProcessor.addGlobalBuff(buff, effect)
        }
    }
}

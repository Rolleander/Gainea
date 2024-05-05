package com.broll.gainea.server.core.cards

import com.broll.gainea.misc.PackageLoader
import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player

class CardStorage(private val game: Game) {
    private val loader: PackageLoader<Card> = PackageLoader(Card::class.java, PACKAGE_PATH)
    val allCards: List<Card>

    init {
        loader.dropClassesOf {
            !it.validFor(game)
        }
        allCards = loader.instantiateAll()
    }

    fun drawRandomCard(player: Player) {
        player.cardHandler.receiveCard(getRandomCard())
    }

    fun getRandomCard() =
        allCards.cloneRandomEntry()

    fun getRandomPlayableCard() =
        allCards.filter { !it.isDirectlyPlayedCard() }.cloneRandomEntry()

    fun getRandomPlayableCard(type: EffectType) =
        allCards.filter { !it.isDirectlyPlayedCard() && it.effectType == type }.cloneRandomEntry()

    fun getRandomDirectlyPlayedCard() =
        allCards.filter { it.isDirectlyPlayedCard() }.cloneRandomEntry()

    private fun List<Card>.cloneRandomEntry(): Card {
        val drawChances = map { it.drawChance }.toFloatArray()
        val totalDrawChances = map { it.drawChance }.sum()
        var chanceSum = 0f
        val chance = RandomUtils.random(0f, totalDrawChances)
        for (i in drawChances.indices) {
            if (chance <= chanceSum) {
                return getCard(get(i).javaClass)
            }
            chanceSum += drawChances[i]
        }
        return getCard(random().javaClass)
    }

    fun getPlayableCards(count: Int) =
        loader.classes.filter { !it.isDirectlyPlayedCard() }
            .shuffled().take(count).map { getCard(it) }


    fun getDirectlyPlayedCards(count: Int) =
        loader.classes.filter { it.isDirectlyPlayedCard() }
            .shuffled().take(count).map { getCard(it) }

    private fun Card.isDirectlyPlayedCard() = this is DirectlyPlayedCard

    private fun Class<out Card>.isDirectlyPlayedCard() =
        DirectlyPlayedCard::class.java.isAssignableFrom(this)

    fun getCard(cardClass: Class<out Card>): Card {
        return loader.instantiate(cardClass)
    }

    companion object {
        private const val PACKAGE_PATH = "com.broll.gainea.server.core.cards.impl"
    }
}

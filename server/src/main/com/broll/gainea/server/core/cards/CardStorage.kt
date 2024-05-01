package com.broll.gainea.server.core.cards

import com.broll.gainea.misc.PackageLoader
import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.player.Player

class CardStorage(private val game: Game) {
    private val loader: PackageLoader<Card> = PackageLoader(Card::class.java, PACKAGE_PATH)
    private val drawChances: FloatArray
    val allCards: List<Card>
    private val totalDrawChances: Float

    init {
        loader.dropClassesOf {
            !it.validFor(game)
        }
        allCards = loader.instantiateAll()
        drawChances = allCards.map { it.drawChance }.toFloatArray()
        totalDrawChances = allCards.map { it.drawChance }.sum()
    }

    fun drawRandomCard(player: Player) {
        val card = getRandomCard()
        player.cardHandler.receiveCard(card)
    }

    fun getRandomCard(): Card {
        var chanceSum = 0f
        val chance = RandomUtils.random(0f, totalDrawChances)
        for (i in drawChances.indices) {
            if (chance <= chanceSum) {
                return loader.instantiate(i)
            }
            chanceSum += drawChances[i]
        }
        return getRandomCard()
    }

    fun getRandomPlayableCard(): Card {
        while (true) {
            val card = getRandomCard()
            if (card !is DirectlyPlayedCard) {
                return card
            }
        }
    }

    fun getRandomDirectlyPlayedCard(): Card {
        while (true) {
            val card = getRandomCard()
            if (card is DirectlyPlayedCard) {
                return card
            }
        }
    }

    fun getPlayableCards(count: Int) =
        loader.classes.filter { !it.isDirectlyPlayedCard() }
            .shuffled().take(count).map { getCard(it) }


    fun getDirectlyPlayedCards(count: Int) =
        loader.classes.filter { it.isDirectlyPlayedCard() }
            .shuffled().take(count).map { getCard(it) }

    private fun Class<out Card>.isDirectlyPlayedCard() =
        DirectlyPlayedCard::class.java.isAssignableFrom(this)

    fun getCard(cardClass: Class<out Card>): Card {
        return loader.instantiate(cardClass)
    }

    companion object {
        private const val PACKAGE_PATH = "com.broll.gainea.server.core.cards.impl"
    }
}

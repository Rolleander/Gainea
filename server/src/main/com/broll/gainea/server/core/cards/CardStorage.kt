package com.broll.gainea.server.core.cards

import com.broll.gainea.misc.PackageLoader
import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.core.GameContainer
import com.broll.gainea.server.core.actions.ActionHandlers
import com.broll.gainea.server.core.player.Player

class CardStorage(private val game: GameContainer, private val actionHandlers: ActionHandlers) {
    private val loader: PackageLoader<Card> = PackageLoader(Card::class.java, PACKAGE_PATH)
    private val drawChances: FloatArray
    private val totalDrawChances: Float

    init {
        val allCards = loader.instantiateAll()
        drawChances = allCards.map { it.drawChance }.toFloatArray()
        totalDrawChances = allCards.map { it.drawChance }.sum()
    }

    fun drawRandomCard(player: Player) {
        val card = getRandomCard()
        player.cardHandler.receiveCard(card)
    }

    fun getRandomCard() : Card{
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
    fun getRandomPlayableCard(): Card
         {
            while (true) {
                val card = getRandomCard()
                if (card is DirectlyPlayedCard) {
                    return card
                }
            }
        }

    fun getPlayableCards(count: Int): List<Card> {
        val classes = ArrayList(loader.classes)
        classes.shuffle()
        val cards = mutableListOf<Card>()
        for (i in classes.indices) {
            val card = loader.instantiate(classes[i])
            if (card !is DirectlyPlayedCard) {
                cards.add(card)
                if (cards.size == count) {
                    break
                }
            }
        }
        return cards
    }

    val allCards: List<Card>
        get() = loader.instantiateAll()

    fun getCard(cardClass: Class<out Card>): Card {
        return loader.instantiate(cardClass)
    }

    companion object {
        private const val PACKAGE_PATH = "com.broll.gainea.server.core.cards.impl"
    }
}

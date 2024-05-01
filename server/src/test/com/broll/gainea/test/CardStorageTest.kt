package com.broll.gainea.test

import com.broll.gainea.server.core.cards.CardStorage
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.init.ExpansionSetting.PLUS_ICELANDS
import org.amshove.kluent.`should be true`
import org.junit.jupiter.api.Test

class CardStorageTest {


    val game = testGame(expansionSetting = PLUS_ICELANDS)
    val storage = CardStorage(game)

    @Test
    fun `gets playable cards`() {
        storage.getPlayableCards(3).all { !DirectlyPlayedCard::class.isInstance(it) }
            .`should be true`()
    }

    @Test
    fun `gets directly played cards`() {
        storage.getDirectlyPlayedCards(3).all {
            DirectlyPlayedCard::class.isInstance(it)
        }.`should be true`()
    }
}
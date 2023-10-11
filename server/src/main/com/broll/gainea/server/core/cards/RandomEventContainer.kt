package com.broll.gainea.server.core.cards

import com.broll.gainea.misc.PackageLoader
import com.broll.gainea.server.core.Game

class RandomEventContainer {
    private val randomEventLoader: PackageLoader<RandomEvent> = PackageLoader(RandomEvent::class.java, PACKAGE_PATH)
    private val randomEvents = randomEventLoader.instantiateAll().shuffled().toMutableList()

    private fun getRandomEvent(): RandomEvent {
        if (randomEvents.isEmpty()) {
            randomEvents.addAll(randomEventLoader.instantiateAll())
            randomEvents.shuffle()
        }
        return randomEvents.removeFirst()
    }

    fun run(game: Game) {
        getRandomEvent().run(game)
    }

    companion object {
        private const val PACKAGE_PATH = "com.broll.gainea.server.core.cards.events.random"
    }
}
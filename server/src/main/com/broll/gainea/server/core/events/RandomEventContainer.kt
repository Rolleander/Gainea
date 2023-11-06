package com.broll.gainea.server.core.events

import com.broll.gainea.misc.PackageLoader
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.utils.ProcessingUtils
import com.broll.gainea.server.core.utils.displayMessage

class RandomEventContainer {
    private val randomEventLoader: PackageLoader<RandomEvent> =
        PackageLoader(RandomEvent::class.java, PACKAGE_PATH)
    private val randomEvents = randomEventLoader.instantiateAll().shuffled().toMutableList()

    private fun getRandomEvent(): RandomEvent {
        if (randomEvents.isEmpty()) {
            randomEvents.addAll(randomEventLoader.instantiateAll())
            randomEvents.shuffle()
        }
        return randomEvents.removeFirst()
    }

    fun run(game: Game) {
        val event = getRandomEvent()
        if (event.init(game)) {
            game.displayMessage("Zufallsereignis!", sound = "long_bwoam.ogg")
            ProcessingUtils.pause(1000)
            event.run()
        }
    }

    companion object {
        private const val PACKAGE_PATH = "com.broll.gainea.server.core.events.random"
    }
}
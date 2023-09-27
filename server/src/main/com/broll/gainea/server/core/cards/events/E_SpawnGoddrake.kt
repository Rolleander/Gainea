package com.broll.gainea.server.core.cards.events

class E_SpawnGoddrake : EventCard(59, "Gaineas Herrscher", "Der Götterdrache erscheint!") {
    override fun play() {
        val dragon = GodDragon()
        val location = LocationUtils.getRandomFree(game.map.allAreas)
        UnitControl.spawn(game, dragon, location) { nt: NT_Event_PlacedObject -> nt.sound = "goddrake.ogg" }
    }

    companion object {
        fun isGoddrakeAlive(game: GameContainer): Boolean {
            return game.objects.stream().anyMatch { it: MapObject? -> it is GodDragon }
        }
    }
}

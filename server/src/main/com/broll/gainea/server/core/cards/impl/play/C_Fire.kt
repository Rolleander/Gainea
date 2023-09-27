package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.net.NT_BoardEffectimport

com.broll.gainea.server.core.cards.Cardimport com.broll.gainea.server.core.map.Areaimport com.broll.gainea.server.core.map.AreaTypeimport com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.MapEffectimport com.broll.gainea.server.core.objects.buffs.TimedEffectimport com.broll.gainea.server.core.utils.LocationUtilsimport java.util.stream.Collectors
class C_Fire : Card(75, "Drachenfeuer", "Wählt ein unbesetztes Gebiet (ausser Seen), dieses kann für " + ROUNDS + " Runden nicht besetzt werden.") {
    override val isPlayable: Boolean
        get() = !targets.isEmpty()
    private val targets: List<Location?>
        private get() = game.map.allAreas.stream().filter { it: Area? -> !LocationUtils.isAreaType(it, AreaType.LAKE) && it!!.isFree }.collect(Collectors.toList())

    override fun play() {
        val target = selectHandler!!.selectLocation(owner, "Welches Gebiet blockieren?", targets)
        val effect = MapEffect(NT_BoardEffect.EFFECT_FIRE, "", target)
        MapEffect.Companion.spawn(game!!, effect)
        target.isTraversable = false
        TimedEffect.Companion.forPlayerRounds(game!!, owner, ROUNDS, object : TimedEffect() {
            override fun unregister() {
                super.unregister()
                MapEffect.Companion.despawn(game!!, effect)
                target.isTraversable = true
            }
        })
    }

    companion object {
        private const val ROUNDS = 5
    }
}

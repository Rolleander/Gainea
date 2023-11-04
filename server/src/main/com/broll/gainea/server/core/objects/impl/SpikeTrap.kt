package com.broll.gainea.server.core.objects.impl

import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.objects.Collectible
import com.broll.gainea.server.core.utils.UnitControl.damage
import com.broll.gainea.server.core.utils.UnitControl.despawn
import com.broll.gainea.server.core.utils.UnitControl.update

class SpikeTrap(game: Game, var damage: Int = 3) :
    Collectible(game, despawn = false, affectNeutral = true) {

    init {
        name = "Stachelfalle"
        updateDesciption()
        icon = 137
        onPickup = {
            dealDamage()
        }
    }

    private fun updateDesciption() {
        description = "Teilt $damage Schaden aus, danach zerfällt sie"
    }

    private fun dealDamage() {
        game.damage(location.units.random())
        damage--
        updateDesciption()
        if (damage > 0) {
            if (location.units.isEmpty()) {
                updateDesciption()
                game.update(this)
            } else {
                dealDamage()
            }
        } else {
            game.despawn(this)
        }
    }
}
package com.broll.gainea.server.core.bot.strategy

import com.broll.gainea.server.core.bot.impl.BotMoveimport

com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.MapObjectimport com.broll.gainea.server.core.objects.Unitimport com.broll.gainea.server.core.objects.monster.Monsterimport com.broll.gainea.server.core.player.Playerimport com.broll.gainea.server.core.utils.PlayerUtilsimport com.google.common.collect.Listsimport org.apache.commons.collections4.map.MultiValueMapimport java.util.stream.Collectors
object LocationDanger {
    fun getFleeToScore(owner: Player, location: Location?): Int {
        val ownerPower = PlayerUtils.getUnits(owner, location).stream().mapToInt { obj: Unit? -> obj.getBattleStrength() }.sum()
        val surroundingEnemyPower = location.getConnectedLocations().stream().flatMap { it: Location? -> it.getInhabitants().stream() }
                .filter { it: MapObject? -> it is Unit && it.canMoveTo(location) && it.getOwner() !== owner }.filter { it: MapObject? ->
                    if (it is Monster) {
                        return@filter it.mightAttackSoon()
                    }
                    true
                }.mapToInt { it: MapObject? -> (it as Unit?).getBattleStrength() }.sum()
        val diff = Math.min(0, surroundingEnemyPower - ownerPower)
        return BotMove.Companion.MOVE_SCORE - diff
    }

    fun getAnnihilationChance(owner: Player, location: Location?): Double {
        return location.getConnectedLocations().stream().mapToDouble { neighbour: Location? ->
            val units = getEnemyUnits(owner, neighbour)
            units.keys.stream().mapToDouble { enemy: Player? -> getAnnihilationChance(location, owner, enemy, units.getCollection(enemy)) }.max().orElse(0.0)
        }.max().orElse(0.0)
    }

    private fun getAnnihilationChance(to: Location?, owner: Player, enemy: Player?, units: Collection<Unit?>): Double {
        val defenders = PlayerUtils.getUnits(owner, to)
        val attackers = ArrayList(units).stream().filter { it: Unit? -> it!!.canMoveTo(to) }.collect(Collectors.toList())
        if (attackers.isEmpty()) {
            return 0
        }
        return if (enemy == null) {
            attackers.stream().filter { it: Unit? -> it is Monster && it.mightAttackSoon() }
                    .mapToDouble { it: Unit? -> BattleSimulation.calculateCurrentWinChance(Lists.newArrayList(it), defenders).toDouble() }.max().orElse(0.0)
        } else {
            BattleSimulation.calculateCurrentWinChance(attackers, defenders).toDouble()
        }
    }

    private fun getEnemyUnits(owner: Player, location: Location?): MultiValueMap<Player?, Unit?> {
        val units = MultiValueMap<Player?, Unit?>()
        location.getInhabitants().stream().filter { it: MapObject? -> it is Unit }.map { it: MapObject? -> it as Unit? }
                .filter { it: Unit? -> it.getOwner() !== owner }.forEach { unit: Unit? -> units[unit.getOwner()] = unit }
        return units
    }
}

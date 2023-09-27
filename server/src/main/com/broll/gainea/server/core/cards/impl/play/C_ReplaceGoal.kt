package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Cardimport

com.broll.gainea.server.core.goals.Goalimport java.util.stream.Collectors
class C_ReplaceGoal : Card(39, "Zielstrategie", "Wähle aus drei neuen Zielen und ersetze damit ein vorhandenes Ziel") {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val goals = game.goalStorage.getAnyGoals(owner, 3)
        val newGoal = goals!![selectHandler!!.selectObject("Wähle ein neues Ziel", goals.stream().map { obj: Goal? -> obj!!.nt() }.collect(Collectors.toList()))]
        val oldGoals = owner.goalHandler.goals
        val oldGoal = oldGoals!![selectHandler!!.selectObject("Welches alte Ziel soll ersetzt werden?", oldGoals.stream().map { obj: Goal? -> obj!!.nt() }.collect(Collectors.toList()))]
        owner.goalHandler.removeGoal(oldGoal)
        owner.goalHandler.newGoal(newGoal)
    }
}

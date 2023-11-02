package com.broll.gainea.server.core.cards.impl.play

import com.broll.gainea.server.core.cards.Card

class C_ReplaceGoal : Card(
    39,
    "Zielstrategie",
    "Wähle aus drei neuen Zielen und ersetze damit ein vorhandenes Ziel"
) {
    init {
        drawChance = 0.5f
    }

    override val isPlayable: Boolean
        get() = true

    override fun play() {
        val goals = game.goalStorage.getRandomGoals(owner, 3)
        val newGoal =
            goals[selectHandler.selectObject("Wähle ein neues Ziel", goals.map { it.nt() })]
        val oldGoals = owner.goalHandler.goals
        val oldGoal = oldGoals[selectHandler.selectObject(
            "Welches alte Ziel soll ersetzt werden?",
            oldGoals.map { it.nt() })]
        owner.goalHandler.removeGoal(oldGoal)
        owner.goalHandler.newGoal(newGoal)
    }
}

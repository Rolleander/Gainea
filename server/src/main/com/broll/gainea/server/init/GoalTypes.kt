package com.broll.gainea.server.init

import com.broll.gainea.server.core.goals.GoalDifficulty
import org.apache.commons.lang3.ArrayUtils

enum class GoalTypes(val goalName: String, private val difficulties: List<GoalDifficulty>) {
    ALL("Alle", GoalDifficulty.entries),
    ONLY_EASY("Nur Einfach", listOf(GoalDifficulty.EASY)),
    ONLY_MEDIUM("Nur Mittel", listOf(GoalDifficulty.MEDIUM)),
    ONLY_HARD("Nur Schwer", listOf(GoalDifficulty.HARD));

    operator fun contains(difficulty: GoalDifficulty) = difficulties.contains(difficulty)
    
}

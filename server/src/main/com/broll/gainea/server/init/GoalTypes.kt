package com.broll.gainea.server.init

import com.broll.gainea.server.core.goals.GoalDifficulty
import org.apache.commons.lang3.ArrayUtils

enum class GoalTypes(override val name: String, private val difficulties: Array<GoalDifficulty>) {
    ALL("Alle", GoalDifficulty.entries.toTypedArray()),
    ONLY_EASY("Nur Einfach", arrayOf(GoalDifficulty.EASY)),
    ONLY_MEDIUM("Nur Mittel", arrayOf(GoalDifficulty.MEDIUM)),
    ONLY_HARD("Nur Schwer", arrayOf(GoalDifficulty.HARD));

    operator fun contains(difficulty: GoalDifficulty?): Boolean {
        return ArrayUtils.contains(difficulties, difficulty)
    }
}

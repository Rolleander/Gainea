package com.broll.gainea.server.init

import com.broll.gainea.net.NT_Lib
import com.broll.gainea.net.NT_Lib_Card
import com.broll.gainea.net.NT_Lib_Fraction
import com.broll.gainea.net.NT_Lib_Goal
import com.broll.gainea.net.NT_Lib_Monster
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.CardStorage
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.server.core.fractions.FractionType
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.GoalStorage
import com.broll.gainea.server.core.objects.monster.GodDragon
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterFactory
import com.broll.gainea.server.core.player.NeutralPlayer
import com.broll.gainea.server.init.ExpansionSetting.FULL
import com.broll.gainea.server.init.GoalTypes.ALL
import com.broll.networklib.server.impl.DummyServerLobby

object ContentLibrary {
    private val lobbyData = LobbyData().apply { expansionSetting = FULL }
    private val game = Game(DummyServerLobby<LobbyData, PlayerData>().apply {
        data = lobbyData
    })
    private val neutralPlayer = NeutralPlayer(game)
    private val fractions = FractionType.entries.map { it.create().library() }
    private val cards = CardStorage(game).allCards.sortedBy { it.title }.map { it.library() }
    private val goals =
        GoalStorage(game, ALL).allGoals.sortedBy { it.libraryText }.flatMap { it.library() }
    private val monsters =
        listOf(
            listOf(
                GodDragon(neutralPlayer)
            ),
            MonsterFactory().createAll(neutralPlayer)
        ).flatten().map { it.library() }.sortedBy { it.stars }

    val nt = NT_Lib().also {
        it.cards = cards.toTypedArray()
        it.fractions = fractions.toTypedArray()
        it.monsters = monsters.toTypedArray()
        it.goals = goals.toTypedArray()
    }

    private fun Fraction.library() = NT_Lib_Fraction().also {
        it.index = FractionType.entries.indexOf(type)
        it.commander = description.commander.nt()
        it.soldier = description.soldier.nt()
        it.name = type.displayName
        it.desc = description.general
        it.descNegative = description.getContra().toTypedArray()
        it.descPositive = description.getPlus().toTypedArray()
    }

    private fun Card.library() = NT_Lib_Card().also {
        it.directlyPlayed = this is DirectlyPlayedCard
        it.picture = picture.toShort()
        it.text = text
        it.title = title
        it.drawChance = drawChance
        it.type = effectType.ordinal.toShort()
    }

    private fun Goal.library(): List<NT_Lib_Goal> {
        init(game, neutralPlayer)
        return libraryDifficulties.map {
            library(it)
        }
    }

    private fun Goal.library(difficulty: GoalDifficulty) = NT_Lib_Goal().also {
        it.description = libraryText
        it.points = difficulty.points.toByte()
        it.restriction = restrictionInfo
    }

    private fun Monster.library() = NT_Lib_Monster().also {
        it.behavior = behavior.ordinal.toByte()
        it.health = health.value.toShort()
        it.power = power.value.toShort()
        it.icon = icon.toShort()
        it.name = name
        it.stars = stars.toByte()
        it.description = description
    }
}


package com.broll.gainea.server.init

import com.broll.gainea.net.proto.NT_Lib
import com.broll.gainea.net.proto.NT_Lib_Card
import com.broll.gainea.net.proto.NT_Lib_Fraction
import com.broll.gainea.net.proto.NT_Lib_Goal
import com.broll.gainea.net.proto.NT_Lib_Monster
import com.broll.gainea.server.core.Game
import com.broll.gainea.server.core.cards.Card
import com.broll.gainea.server.core.cards.CardStorage
import com.broll.gainea.server.core.cards.DirectlyPlayedCard
import com.broll.gainea.server.core.fractions.Fraction
import com.broll.gainea.core.fractions.FractionType
import com.broll.gainea.server.core.fractions.create
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.core.goals.GoalDifficulty
import com.broll.gainea.server.core.goals.GoalStorage
import com.broll.gainea.server.core.objects.monster.GodDragon
import com.broll.gainea.server.core.objects.monster.Monster
import com.broll.gainea.server.core.objects.monster.MonsterFactory
import com.broll.gainea.server.core.player.NeutralPlayer
import com.broll.gainea.core.map.ExpansionSetting.FULL
import com.broll.gainea.core.goals.GoalTypes.ALL
import com.broll.mpnll.server.MpnllServer

object ContentLibrary {
    private val lobbyData = LobbyData().apply { expansionSetting = FULL }
    private val game = Game(MpnllServer().lobbyHandler.openLobby {
        it.name = "content-library"
        it.data = lobbyData
        it.isHidden = true
        it.isAutoClose = false
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

    val nt: NT_Lib = NT_Lib.newBuilder()
        .addAllCards(cards)
        .addAllFractions(fractions)
        .addAllMonsters(monsters)
        .addAllGoals(goals)
        .build()

    private fun Fraction.library() = NT_Lib_Fraction.newBuilder()
        .setIndex(FractionType.entries.indexOf(type))
        .setCommander(description.commander.nt())
        .setSoldier(description.soldier.nt())
        .setName(type.displayName)
        .setDesc(description.general)
        .addAllDescNegative(description.getContra())
        .addAllDescPositive(description.getPlus())
        .build()

    private fun Card.library() = NT_Lib_Card.newBuilder()
        .setDirectlyPlayed(this is DirectlyPlayedCard)
        .setPicture(picture)
        .setText(text)
        .setTitle(title)
        .setDrawChance(drawChance)
        .setType(effectType.ordinal)
        .build()

    private fun Goal.library(): List<NT_Lib_Goal> {
        init(game, neutralPlayer)
        return libraryDifficulties.map {
            library(it)
        }
    }

    private fun Goal.library(difficulty: GoalDifficulty): NT_Lib_Goal {
        val builder = NT_Lib_Goal.newBuilder()
            .setDescription(libraryText)
            .setPoints(difficulty.points)
        restrictionInfo?.let(builder::setRestriction)
        return builder.build()
    }

    private fun Monster.library(): NT_Lib_Monster {
        val builder = NT_Lib_Monster.newBuilder()
            .setBehavior(behavior.ordinal)
            .setHealth(health.value)
            .setPower(power.value)
            .setIcon(icon)
            .setName(name)
            .setStars(stars)
        description?.let(builder::setDescription)
        return builder.build()
    }
}

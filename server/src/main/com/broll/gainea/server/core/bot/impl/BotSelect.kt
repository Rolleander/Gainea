package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.net.proto.NT_Action_SelectChoice
import com.broll.gainea.net.proto.NT_Choice
import com.broll.gainea.net.proto.NT_Goal
import com.broll.gainea.net.proto.NT_Reaction
import com.broll.gainea.net.proto.NT_Unit
import com.broll.gainea.server.core.bot.BotAction
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import org.slf4j.LoggerFactory
import com.google.protobuf.Message

class BotSelect : BotAction<NT_Action_SelectChoice>() {
    private val chooseOption = ArrayDeque<Selection>()
    override fun react(action: NT_Action_SelectChoice, reaction: NT_Reaction.Builder) {
        val option = pollChooseOption()
        if (option != null) {
            try {
                reaction.setOption(option.select(action))
                return
            } catch (e: Exception) {
                Log.error("Invalid selection setup, chooses randomly instead", e)
            }
        }
        if (action.objectChoicesCount > 0) {
            reaction.setOption(RandomUtils.random(0, action.objectChoicesCount - 1))
        } else {
            reaction.setOption(RandomUtils.random(0, action.choicesCount - 1))
        }
    }

    override val actionClass: Class<out Message>
        get() = NT_Action_SelectChoice::class.java

    fun selectText(selection: (List<String>) -> String): TextSelection {
        return TextSelection(selection)
    }

    fun selectUnit(selection: (List<Unit>) -> Unit): UnitSelection {
        return UnitSelection(selection)
    }

    fun selectGoal(selection: (List<Goal>) -> Goal): GoalSelection {
        return GoalSelection(selection)
    }

    fun selectLocation(selection: (List<Location>) -> Location): LocationSelection {
        return LocationSelection(selection)
    }

    fun select(option: Int): SelectOption {
        return SelectOption(option)
    }

    fun clearSelections() {
        chooseOption.clear()
    }

    fun nextChooseOption(option: Selection) {
        chooseOption.add(option)
    }

    private fun pollChooseOption() = chooseOption.lastOrNull()


    interface Selection {
        fun select(action: NT_Action_SelectChoice): Int
    }

    inner class SelectOption(private val option: Int) : Selection {
        override fun select(action: NT_Action_SelectChoice): Int {
            return option
        }
    }

    open class ObjectSelection<O> internal constructor(provider: (List<O>) -> O, mapper: (NT_Choice) -> O) : Selection {
        private val provider: (List<O>) -> O
        private val mapper: (NT_Choice) -> O

        init {
            this.provider = provider
            this.mapper = mapper
        }

        override fun select(action: NT_Action_SelectChoice): Int {
            val options = action.objectChoicesList.map { mapper(it) }
            return options.indexOf(provider(options))
        }
    }

    inner class TextSelection(private val provider: (List<String>) -> String) : Selection {
        override fun select(action: NT_Action_SelectChoice): Int {
            val options = action.choicesList
            return options.indexOf(provider(options))
        }
    }

    inner class LocationSelection internal constructor(provider: (List<Location>) -> Location) : ObjectSelection<Location>(provider, { game.map.getLocation(it.integerValue) })
    inner class UnitSelection internal constructor(provider: (List<Unit>) -> Unit) : ObjectSelection<Unit>(provider, {
        if (it.hasBoardObject()) BotUtils.getObject(game, it.boardObject) else BotUtils.getObject(game, it.unit)
    })
    inner class GoalSelection internal constructor(provider: (List<Goal>) -> Goal) : ObjectSelection<Goal>(provider, { BotUtils.getGoal(game, it.goal) })
    companion object {
        private val Log = LoggerFactory.getLogger(BotSelect::class.java)
    }
}

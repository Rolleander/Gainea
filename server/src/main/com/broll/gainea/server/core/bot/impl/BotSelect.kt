package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.misc.RandomUtils
import com.broll.gainea.net.NT_Action
import com.broll.gainea.net.NT_Action_SelectChoice
import com.broll.gainea.net.NT_Goal
import com.broll.gainea.net.NT_Reaction
import com.broll.gainea.net.NT_Unit
import com.broll.gainea.server.core.bot.BotAction
import com.broll.gainea.server.core.bot.BotUtils
import com.broll.gainea.server.core.goals.Goal
import com.broll.gainea.server.core.map.Location
import com.broll.gainea.server.core.objects.Unit
import org.slf4j.LoggerFactory

class BotSelect : BotAction<NT_Action_SelectChoice>() {
    private val chooseOption = ArrayDeque<Selection>()
    override fun react(action: NT_Action_SelectChoice, reaction: NT_Reaction) {
        val option = pollChooseOption()
        if (option != null) {
            try {
                reaction.option = option.select(action)
                return
            } catch (e: Exception) {
                Log.error("Invalid selection setup, chooses randomly instead", e)
            }
        }
        if (action.objectChoices != null) {
            reaction.option = RandomUtils.random(0, action.objectChoices.size - 1)
        } else {
            reaction.option = RandomUtils.random(0, action.choices.size - 1)
        }
    }

    override val actionClass: Class<out NT_Action>
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

    open class ObjectSelection<O> internal constructor(provider: (List<O>) -> O, mapper: (Any) -> O) : Selection {
        private val provider: (List<O>) -> O
        private val mapper: (Any) -> O

        init {
            this.provider = provider
            this.mapper = mapper
        }

        override fun select(action: NT_Action_SelectChoice): Int {
            val options = action.objectChoices.map { mapper(it) }
            return options.indexOf(provider(options))
        }
    }

    inner class TextSelection(private val provider: (List<String>) -> String) : Selection {
        override fun select(action: NT_Action_SelectChoice): Int {
            val options = action.choices.toList()
            return options.indexOf(provider(options))
        }
    }

    inner class LocationSelection internal constructor(provider: (List<Location>) -> Location) : ObjectSelection<Location>(provider, { game.map.getLocation(it as Int) })
    inner class UnitSelection internal constructor(provider: (List<Unit>) -> Unit) : ObjectSelection<Unit>(provider, { BotUtils.getObject(game, it as NT_Unit) })
    inner class GoalSelection internal constructor(provider: (List<Goal>) -> Goal) : ObjectSelection<Goal>(provider, { BotUtils.getGoal(game, it as NT_Goal) })
    companion object {
        private val Log = LoggerFactory.getLogger(BotSelect::class.java)
    }
}

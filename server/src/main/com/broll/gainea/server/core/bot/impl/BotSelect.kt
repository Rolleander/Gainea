package com.broll.gainea.server.core.bot.impl

import com.broll.gainea.net.NT_Actionimport

com.broll.gainea.net.NT_Action_SelectChoiceimport com.broll.gainea.net.NT_Goalimport com.broll.gainea.net.NT_Reactionimport com.broll.gainea.net.NT_Unitimport com.broll.gainea.server.core.bot.BotActionimport com.broll.gainea.server.core.bot.BotUtilsimport com.broll.gainea.server.core.goals.Goal com.broll.gainea.misc.RandomUtils
import com.broll.gainea.server.init.ServerSetup
import com.broll.gainea.server.ServerStatisticimport

com.broll.gainea.server.core.map.Locationimport com.broll.gainea.server.core.objects.Unit
import com.broll.networklib.server.LobbyServerCLI
import com.broll.networklib.server.LobbyServerCLI.CliCommand
import com.broll.networklib.server.ICLIExecutor
import kotlin.Throws
import com.broll.networklib.server.ILobbyServerListenerimport

org.slf4j.LoggerFactoryimport java.lang.Exceptionimport java.util.ArrayDequeimport java.util.Arraysimport java.util.Queueimport java.util.function.Functionimport java.util.stream.Collectors
class BotSelect : BotAction<NT_Action_SelectChoice>() {
    private val chooseOption: Queue<Selection?> = ArrayDeque()
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

    override val actionClass: Class<out NT_Action?>?
        get() = NT_Action_SelectChoice::class.java

    fun selectText(selection: SelectProvider<String?>?): TextSelection {
        return TextSelection(selection)
    }

    fun selectUnit(selection: SelectProvider<Unit?>): UnitSelection {
        return UnitSelection(selection)
    }

    fun selectGoal(selection: SelectProvider<Goal?>): GoalSelection {
        return GoalSelection(selection)
    }

    fun selectLocation(selection: SelectProvider<Location?>): LocationSelection {
        return LocationSelection(selection)
    }

    fun select(option: Int): SelectOption {
        return SelectOption(option)
    }

    fun clearSelections() {
        chooseOption.clear()
    }

    fun nextChooseOption(option: Selection?) {
        chooseOption.offer(option)
    }

    private fun pollChooseOption(): Selection? {
        return chooseOption.poll()
    }

    interface SelectProvider<O> {
        fun select(options: List<O>?): O
    }

    interface Selection {
        fun select(action: NT_Action_SelectChoice): Int
    }

    inner class SelectOption(private val option: Int) : Selection {
        override fun select(action: NT_Action_SelectChoice): Int {
            return option
        }
    }

    private open class ObjectSelection<O> internal constructor(provider: SelectProvider<O?>, mapper: Function<Any, O?>) : Selection {
        private val provider: SelectProvider<O>
        private val mapper: Function<Any, O>

        init {
            this.provider = provider
            this.mapper = mapper
        }

        override fun select(action: NT_Action_SelectChoice): Int {
            val options = Arrays.stream(action.objectChoices).map { it: Any -> mapper.apply(it) }.collect(Collectors.toList())
            return options.indexOf(provider.select(options))
        }
    }

    inner class TextSelection(private val provider: SelectProvider<String>) : Selection {
        override fun select(action: NT_Action_SelectChoice): Int {
            val options = Arrays.asList(*action.choices)
            return options.indexOf(provider.select(options))
        }
    }

    inner class LocationSelection internal constructor(provider: SelectProvider<Location?>) : ObjectSelection<Location?>(provider, Function { it: Any -> game.map.getLocation(it as Int) })
    inner class UnitSelection internal constructor(provider: SelectProvider<Unit?>) : ObjectSelection<Unit?>(provider, Function { it: Any -> BotUtils.getObject(game!!, it as NT_Unit) })
    inner class GoalSelection internal constructor(provider: SelectProvider<Goal?>) : ObjectSelection<Goal?>(provider, Function { it: Any -> BotUtils.getGoal(game!!, it as NT_Goal) })
    companion object {
        private val Log = LoggerFactory.getLogger(BotSelect::class.java)
    }
}

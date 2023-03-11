package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.net.NT_Action_SelectChoice;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.bot.BotAction;
import com.broll.gainea.server.core.bot.BotUtils;
import com.broll.gainea.server.core.goals.Goal;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.objects.Unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BotSelect extends BotAction<NT_Action_SelectChoice> {
    private final static Logger Log = LoggerFactory.getLogger(BotSelect.class);

    private final Queue<Selection> chooseOption = new ArrayDeque<>();

    @Override
    protected void react(NT_Action_SelectChoice action, NT_Reaction reaction) {
        BotSelect.Selection option = pollChooseOption();
        if (option != null) {
            try {
                reaction.option = option.select(action);
                return;
            } catch (Exception e) {
                Log.error("Invalid selection setup, chooses randomly instead", e);
            }
        }
        if (action.objectChoices != null) {
            reaction.option = RandomUtils.random(0, action.objectChoices.length - 1);
        } else {
            reaction.option = RandomUtils.random(0, action.choices.length - 1);
        }
    }

    @Override
    public Class<NT_Action_SelectChoice> getActionClass() {
        return NT_Action_SelectChoice.class;
    }


    public TextSelection selectText(SelectProvider<String> selection) {
        return new TextSelection(selection);
    }

    public UnitSelection selectUnit(SelectProvider<Unit> selection) {
        return new UnitSelection(selection);
    }

    public GoalSelection selectGoal(SelectProvider<Goal> selection) {
        return new GoalSelection(selection);
    }

    public LocationSelection selectLocation(SelectProvider<Location> selection) {
        return new LocationSelection(selection);
    }

    public SelectOption select(int option) {
        return new SelectOption(option);
    }

    void clearSelections() {
        chooseOption.clear();
    }

    void nextChooseOption(BotSelect.Selection option) {
        chooseOption.offer(option);
    }

    private BotSelect.Selection pollChooseOption() {
        return chooseOption.poll();
    }

    public interface SelectProvider<O> {
        O select(List<O> options);
    }

    public interface Selection {
        int select(NT_Action_SelectChoice action);
    }

    private class SelectOption implements Selection {
        private int option;

        public SelectOption(int option) {
            this.option = option;
        }

        @Override
        public int select(NT_Action_SelectChoice action) {
            return option;
        }
    }

    private static class ObjectSelection<O> implements Selection {

        private SelectProvider<O> provider;
        private Function<Object, O> mapper;

        ObjectSelection(SelectProvider<O> provider, Function<Object, O> mapper) {
            this.provider = provider;
            this.mapper = mapper;
        }

        @Override
        public int select(NT_Action_SelectChoice action) {
            List<O> options = Arrays.stream(action.objectChoices).map(it -> mapper.apply(it)).collect(Collectors.toList());
            return options.indexOf(provider.select(options));
        }
    }

    private class TextSelection implements Selection {
        private SelectProvider<String> provider;

        public TextSelection(SelectProvider<String> provider) {
            this.provider = provider;
        }

        @Override
        public int select(NT_Action_SelectChoice action) {
            List<String> options = Arrays.asList(action.choices);
            return options.indexOf(provider.select(options));
        }
    }

    private class LocationSelection extends ObjectSelection<Location> {

        LocationSelection(SelectProvider<Location> provider) {
            super(provider, it -> game.getMap().getLocation((Integer) it));
        }
    }

    private class UnitSelection extends ObjectSelection<Unit> {

        UnitSelection(SelectProvider<Unit> provider) {
            super(provider, it -> BotUtils.getObject(game, (NT_Unit) it));
        }
    }

    private class GoalSelection extends ObjectSelection<Goal> {

        GoalSelection(SelectProvider<Goal> provider) {
            super(provider, it -> BotUtils.getGoal(game, (NT_Goal) it));
        }
    }
}

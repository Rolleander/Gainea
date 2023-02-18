package com.broll.gainea.server.core.bot.impl;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.net.NT_Action_SelectChoice;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.server.core.bot.BotAction;

import java.util.ArrayDeque;
import java.util.Queue;

public class BotSelect extends BotAction<NT_Action_SelectChoice> {

    private final Queue<Integer> chooseOption = new ArrayDeque<>();

    public void nextChooseOption(int option) {
        chooseOption.offer(option);
    }


    @Override
    protected void react(NT_Action_SelectChoice action, NT_Reaction reaction) {
        Integer option = chooseOption.poll();
        if (option != null) {
            reaction.option = option;
            return;
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

}

package com.broll.gainea.server.core.bot;

import com.broll.gainea.misc.RandomUtils;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Card;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_Action_SelectChoice;
import com.broll.gainea.net.NT_Battle_Reaction;
import com.broll.gainea.net.NT_Battle_Start;
import com.broll.gainea.net.NT_Battle_Update;
import com.broll.gainea.net.NT_EndTurn;
import com.broll.gainea.net.NT_LoadedGame;
import com.broll.gainea.net.NT_PlayerAction;
import com.broll.gainea.net.NT_PlayerTurnActions;
import com.broll.gainea.net.NT_Reaction;
import com.broll.gainea.net.NT_StartGame;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.battle.BattleHandler;
import com.broll.gainea.server.core.utils.ProcessingUtils;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.impl.BotSite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class RandomBot extends BotSite<PlayerData> {
    private final static Logger Log = LoggerFactory.getLogger(RandomBot.class);

    private boolean allowRetreat = false;

    @PackageReceiver
    public void gameStart(NT_StartGame start) {
        Log.info(this.getBot() + " Bot send loaded Game!");
        sendServer(new NT_LoadedGame());
    }

    @PackageReceiver
    public void endTurn(NT_EndTurn nt) {
        //can only end turn, does not wait
        endTurn();
    }

    @PackageReceiver
    public void battleUpdate(NT_Battle_Start start) {
        allowRetreat = start.allowRetreat && start.attacker == getBot().getId();
    }

    @PackageReceiver
    public void battleUpdate(NT_Battle_Update update) {
        ProcessingUtils.pause(BattleHandler.getAnimationDelay(update.attackerRolls.length, update.defenderRolls.length));
        if (allowRetreat) {
            //never retreats
            NT_Battle_Reaction nt = new NT_Battle_Reaction();
            nt.keepAttacking = true;
            sendServer(nt);
        }
    }

    private void endTurn() {
        sendServer(new NT_EndTurn());
    }

    @PackageReceiver
    public void turnOptions(NT_PlayerTurnActions actions) {
        Log.info(getBot() + " handle optional actions");
        if (RandomUtils.randomBoolean(0.3f)) {
            //just ends his turn
            endTurn();
        } else {
            //filter card actions
            NT_Action_Card[] cardActions = Arrays.stream(actions.actions).filter(it -> it instanceof NT_Action_Card).map(it -> (NT_Action_Card) it).toArray(NT_Action_Card[]::new);
            NT_Action[] moveOrAttackActions = Arrays.stream(actions.actions).filter(it -> it instanceof NT_Action_Move || it instanceof NT_Action_Attack).toArray(NT_Action[]::new);
            if (moveOrAttackActions.length == 0) {
                endTurn();
                return;
            }
            //picks random move or attack action
            int nr = RandomUtils.random(0, moveOrAttackActions.length - 1);
            NT_Action action = moveOrAttackActions[nr];
            NT_Reaction reaction = new NT_Reaction();
            reaction.actionId = action.actionId;
            if (action instanceof NT_Action_Move) {
                int options = ((NT_Action_Move) action).possibleLocations.length;
                reaction.option = RandomUtils.random(0, options - 1);
                reaction.options = pickRandomUnits(((NT_Action_Move) action).units);
            } else if (action instanceof NT_Action_Attack) {
                int options = ((NT_Action_Attack) action).attackLocations.length;
                reaction.option = RandomUtils.random(0, options - 1);
                reaction.options = pickRandomUnits(((NT_Action_Attack) action).units);
            }
            sendServer(reaction);
        }
    }

    private int[] pickRandomUnits(NT_Unit[] units) {
        int count;
        if (RandomUtils.randomBoolean(0.5f)) {
            //pick all
            count = units.length;
        } else {
            //pick random count
            count = RandomUtils.random(1, units.length);
        }
        int[] pick = new int[count];
        for (int i = 0; i < pick.length; i++) {
            pick[i] = i;
        }
        return pick;
    }

    @PackageReceiver
    public void handleAction(NT_PlayerAction requiredAction) {
        Log.info(getBot() + " handle required action");
        //picks random option
        NT_Action action = requiredAction.action;
        NT_Reaction reaction = new NT_Reaction();
        reaction.actionId = action.actionId;
        if (action instanceof NT_Action_PlaceUnit) {
            int options = ((NT_Action_PlaceUnit) action).possibleLocations.length;
            reaction.option = RandomUtils.random(0, options - 1);
        } else if (action instanceof NT_Action_SelectChoice) {
            int options = 1;
            if (((NT_Action_SelectChoice) action).objectChoices != null) {
                options = ((NT_Action_SelectChoice) action).objectChoices.length;
            } else {
                options = ((NT_Action_SelectChoice) action).choices.length;
            }
            reaction.option = RandomUtils.random(0, options - 1);
        }
        sendServer(reaction);
    }

}

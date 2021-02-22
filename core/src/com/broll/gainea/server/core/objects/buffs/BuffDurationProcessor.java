package com.broll.gainea.server.core.objects.buffs;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.utils.UnitControl;

import org.apache.commons.collections4.map.MultiValueMap;

import java.util.Collection;

public class BuffDurationProcessor extends GameUpdateReceiverAdapter {

    private GameContainer game;
    private int currentTurnCount;

    private MultiValueMap<Integer, AbstractBuff> timedBuffs = new MultiValueMap<>();

    public BuffDurationProcessor(GameContainer game) {
        this.game = game;
        game.getUpdateReceiver().register(this);
    }

    @Override
    public void turnStarted(Player player) {
        currentTurnCount++;
        Collection<AbstractBuff> timedout = timedBuffs.getCollection(currentTurnCount);
        timedout.forEach(this::timedout);
        timedBuffs.remove(currentTurnCount);
    }

    private void timedout(AbstractBuff buff) {
        //check if any unit died because of removal of buffs
        for (Object object : buff.getAffectedObjects()) {
            if (object instanceof BattleObject) {
                BattleObject unit = (BattleObject) object;
                if (unit.isDead()) {
                    UnitControl.kill(game, unit);
                }
            }
        }
        buff.remove();
    }

    public void timeoutBuff(AbstractBuff buff, int turns) {
        int timeoutTurn = currentTurnCount + game.getPlayers().size() * Math.min(1, turns);
        timedBuffs.put(timeoutTurn, buff);
    }

}

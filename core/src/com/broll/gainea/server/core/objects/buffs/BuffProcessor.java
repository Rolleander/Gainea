package com.broll.gainea.server.core.objects.buffs;

import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;
import com.broll.gainea.server.core.processing.GameUpdateReceiverAdapter;
import com.broll.gainea.server.core.processing.IGameUpdateReceiver;
import com.broll.gainea.server.core.utils.UnitControl;

import org.apache.commons.collections4.map.MultiValueMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuffProcessor extends GameUpdateReceiverAdapter {

    private GameContainer game;
    private int currentTurnCount;

    private MultiValueMap<Integer, Buff> timedBuffs = new MultiValueMap<>();
    private Map<Buff, GlobalBuff> gloabalBuffs = new HashMap<>();

    public BuffProcessor(GameContainer game) {
        this.game = game;
        game.getUpdateReceiver().register(this);
    }

    @Override
    public void turnStarted(Player player) {
        currentTurnCount++;
        Collection<Buff> timedout = timedBuffs.getCollection(currentTurnCount);
        if (timedout != null) {
            timedout.forEach(this::timedout);
            timedBuffs.remove(currentTurnCount);
        }
    }

    private void timedout(Buff buff) {
        List<Object> affectedObjects = new ArrayList<>(buff.getAffectedObjects());
        buff.remove();
        gloabalBuffs.remove(buff);
        //check if any unit died because of removal of buffs
        for (Object object : affectedObjects) {
            if (object instanceof BattleObject) {
                BattleObject unit = (BattleObject) object;
                if (unit.isDead()) {
                    UnitControl.kill(game, unit);
                }
            }
        }
    }

    public void timeoutBuff(Buff buff, int turns) {
        int timeoutTurn = currentTurnCount + game.getPlayers().size() * Math.max(1, turns);
        timedBuffs.put(timeoutTurn, buff);
    }

    public void addGlobalBuff(GlobalBuff globalBuff, int effect) {
        this.gloabalBuffs.put(globalBuff.getBuff(), globalBuff);
        //apply directly to all existing units for the targets
        globalBuff.getTargets().forEach(target -> {
            List<BattleObject> units;
            if (target == null) {
                units = game.getObjects().stream().filter(it -> it instanceof BattleObject).map(it -> (BattleObject) it).collect(Collectors.toList());
            } else {
                units = target.getUnits();
            }
            units.forEach(globalBuff::apply);
            UnitControl.focus(game, units, effect);
        });
    }

    public void applyGlobalBuffs(BattleObject unit) {
        Player owner = unit.getOwner();
        gloabalBuffs.values().stream().filter(it -> it.getTargets().contains(owner)).forEach(buff -> buff.apply(unit));
    }

}

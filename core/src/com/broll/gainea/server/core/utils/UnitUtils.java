package com.broll.gainea.server.core.utils;

import com.broll.gainea.net.NT_Event_FocusObject;
import com.broll.gainea.server.core.GameContainer;
import com.broll.gainea.server.core.objects.BattleObject;
import com.broll.gainea.server.core.player.Player;

public class UnitUtils {
    public static void damageUnit(GameContainer game, BattleObject unit, int damage) {
        damageUnit(game, unit, damage, 0);
    }

    public static void damageUnit(GameContainer game, BattleObject unit, int damage, int screenEffect) {
        unit.takeDamage(damage);
        if (unit.isDead()) {
            //remove unit
            Player owner = unit.getOwner();
            if (owner == null) {
                game.getObjects().remove(unit);
            } else {
                owner.getUnits().remove(unit);
            }
        }
        //send update to focus clients on object
        NT_Event_FocusObject nt = new NT_Event_FocusObject();
        nt.object = unit.nt();
        nt.screen_effect = screenEffect;
        game.getReactionHandler().getActionHandlers().getReactionActions().sendGameUpdate(nt);
    }

}

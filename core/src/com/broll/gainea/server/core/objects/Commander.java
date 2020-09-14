package com.broll.gainea.server.core.objects;

import com.broll.gainea.server.core.player.Player;

public class Commander extends Soldier {
    public Commander(Player owner) {
        super(owner);
        setIcon(1);
    }
}

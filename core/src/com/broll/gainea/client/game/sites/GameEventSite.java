package com.broll.gainea.client.game.sites;

import com.broll.gainea.net.NT_Event_Bundle;
import com.broll.gainea.net.NT_Event_DrawedCard;
import com.broll.gainea.net.NT_Event_FocusLocation;
import com.broll.gainea.net.NT_Event_FocusObject;
import com.broll.gainea.net.NT_Event_MovedObject;
import com.broll.gainea.net.NT_Event_PlacedObject;
import com.broll.gainea.net.NT_Event_PlayedCard;
import com.broll.gainea.net.NT_Event_ReceivedGoal;
import com.broll.gainea.net.NT_Event_TextInfo;
import com.broll.networklib.PackageReceiver;

public class GameEventSite extends AbstractGameSite {


    @PackageReceiver
    public void received(NT_Event_Bundle bundle) {

    }

    @PackageReceiver
    public void received(NT_Event_TextInfo text) {

    }

    @PackageReceiver
    public void received(NT_Event_DrawedCard card) {

    }

    @PackageReceiver
    public void received(NT_Event_FocusObject focus) {

    }

    @PackageReceiver
    public void received(NT_Event_MovedObject moved) {

    }

    @PackageReceiver
    public void received(NT_Event_PlacedObject placed) {

    }

    @PackageReceiver
    public void received(NT_Event_PlayedCard card) {

    }

    @PackageReceiver
    public void received(NT_Event_FocusLocation location) {

    }

    @PackageReceiver
    public void received(NT_Event_ReceivedGoal goal) {

    }

}
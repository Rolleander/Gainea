package com.broll.gainea.client.game;

import com.broll.gainea.net.proto.NT_Action;

public interface PlayerPerformAction {

    void perform(NT_Action action, int option, int[] options);
}

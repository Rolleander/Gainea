package com.broll.gainea.client.game;

import com.broll.gainea.net.proto.*;

public final class ClientActions {
    private ClientActions() {}
    public static NT_Action wrap(NT_Action_Move value) { return NT_Action.newBuilder().setMove(value).build(); }
    public static NT_Action wrap(NT_Action_Attack value) { return NT_Action.newBuilder().setAttack(value).build(); }
    public static NT_Action wrap(NT_Action_Card value) { return NT_Action.newBuilder().setCard(value).build(); }
    public static NT_Action wrap(NT_Action_Shop value) { return NT_Action.newBuilder().setShop(value).build(); }
}

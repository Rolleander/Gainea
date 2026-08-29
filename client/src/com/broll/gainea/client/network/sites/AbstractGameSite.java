package com.broll.gainea.client.network.sites;

import com.badlogic.gdx.Gdx;
import com.broll.gainea.Gainea;
import com.broll.mpnll.client.lobby.User;
import com.broll.mpnll.client.site.ClientSite;
import com.broll.mpnll.client.site.MessageReceiverRegistry;
import com.google.protobuf.Message;

import java.util.function.Consumer;

public abstract class AbstractGameSite extends ClientSite {
    protected Gainea game;
    public void init(Gainea game) { this.game = game; }
    protected <T extends Message> void on(MessageReceiverRegistry registry, Class<T> type, Consumer<T> receiver) {
        registry.<T>connect(type, message -> Gdx.app.postRunnable(() -> receiver.accept(message)));
    }
    protected User getPlayer() { return getLobby().getMyUser(); }
}

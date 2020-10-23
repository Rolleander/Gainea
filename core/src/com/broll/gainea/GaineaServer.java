package com.broll.gainea;

import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.LobbyFactory;
import com.broll.gainea.server.init.NetworkSetup;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.network.nt.NT_LobbyKicked;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.LobbyServerCLI;
import com.broll.networklib.server.impl.BotSite;
import com.broll.networklib.server.impl.Player;
import com.broll.networklib.server.impl.ServerLobby;
import com.broll.networklib.server.impl.ServerLobbyListener;
import com.esotericsoftware.minlog.Log;

public class GaineaServer {

    public static void main(String[] args) {
//        Log.set(Log.LEVEL_INFO);
        Log.DEBUG();
        LobbyGameServer<LobbyData, PlayerData> server = new LobbyGameServer<>("GaineaServer", NetworkSetup::registerNetwork);
        NetworkSetup.setup(server);
        server.open();
        ServerLobby<LobbyData, PlayerData> lobby = server.getLobbyHandler().openLobby("Testlobby");
        LobbyFactory.initLobby(lobby, ExpansionSetting.BASIC_GAME);
        lobby.setAutoClose(false);
     /*   PlayerData data = new PlayerData();
        data.setReady(true);
        lobby.createBot("bot_hans", data).ifPresent(bot -> {
            bot.register(new BotSite<PlayerData>() {
                @PackageReceiver
                void rec(NT_LobbyKicked f) {

                }
            });
        });*/
        LobbyServerCLI.open(server);
    }


}

package com.broll.gainea;

import com.broll.gainea.server.ExpansionSetting;
import com.broll.gainea.server.LobbyData;
import com.broll.gainea.server.NetworkSetup;
import com.broll.gainea.server.PlayerData;
import com.broll.networklib.NetworkRegister;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.LobbyServerCLI;
import com.broll.networklib.server.impl.ServerLobby;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

public class GaineaServer {

    public static void main(String[] args) {
        LobbyGameServer<LobbyData, PlayerData> server = new LobbyGameServer<>("GaineaServer", NetworkSetup::registerNetwork);
        NetworkSetup.setup(server);
        server.open();
        ServerLobby<LobbyData, PlayerData> lobby = server.getLobbyHandler().openLobby("Testlobby");
        LobbyData lobbyData = new LobbyData();
        // lobbyData.setExpansionSetting(ExpansionSetting.BASIC_GAME);
        lobby.setData(lobbyData);
        System.out.println("Server open!");
        cli(server);
    }

    private static void cli(LobbyGameServer<LobbyData, PlayerData> server) {
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(System.in));
        LobbyServerCLI cli = server.initCLI();
        do {
            try {
                String input = reader.readLine();
                if (cli.hanldeInput(input)) {
                    //server stopped
                    return;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } while (true);
    }
}

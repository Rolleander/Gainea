package com.broll.gainea;

import com.broll.gainea.server.LobbyData;
import com.broll.gainea.server.NetworkSetup;
import com.broll.gainea.server.PlayerData;
import com.broll.networklib.NetworkRegister;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.LobbyServerCLI;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;

public class GaineaServer {

    public static void main(String[] args) {
        LobbyGameServer<LobbyData, PlayerData> server = new LobbyGameServer<>("GaineaServer");
        NetworkSetup.setup(server);
        server.open();
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

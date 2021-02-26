package com.broll.gainea.desktop;

import com.broll.gainea.GaineaServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerLauncher {

    public static void main(String[] args) {
        System.setProperty("log4j.configuration", "log4j_server.properties");
        Logger Log = LoggerFactory.getLogger(ServerLauncher.class);
        try {
            GaineaServer server = new GaineaServer();
            server.openTestLobby();
            server.appendCLI();
        }catch (Exception e){
            Log.error("Failed to launch server!",e);
        }
    }

}

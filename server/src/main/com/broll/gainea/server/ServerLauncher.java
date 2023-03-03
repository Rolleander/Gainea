package com.broll.gainea.server;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ServerLauncher {

    public static void main(String[] args) throws IOException {
        System.setProperty("log4j.configuration", "log4j_server.properties");
        Logger Log = LoggerFactory.getLogger(ServerLauncher.class);
        String version =
                new BufferedReader(
                        new InputStreamReader(ServerLauncher.class.getResourceAsStream("/version.txt"), StandardCharsets.UTF_8)).readLine();
        try {
            GaineaServer server = new GaineaServer(version);
            server.appendCLI();
        } catch (Exception e) {
            Log.error("Failed to launch server!", e);
        }
    }

}

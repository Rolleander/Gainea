package com.broll.gainea.server;


import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class ServerLauncher {

    public static void main(String[] args) throws IOException {
        System.setProperty("log4j.configuration", "log4j_server.properties");
        Logger Log = LoggerFactory.getLogger(ServerLauncher.class);
        String version = IOUtils.readLines(ServerLauncher.class.getResourceAsStream("/version.txt"), StandardCharsets.UTF_8).get(0);
        try {
            GaineaServer server = new GaineaServer(version);
            server.appendCLI();
        } catch (Exception e) {
            Log.error("Failed to launch server!", e);
        }
    }

}

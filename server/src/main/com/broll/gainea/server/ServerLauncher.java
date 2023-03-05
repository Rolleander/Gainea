package com.broll.gainea.server;


import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ServerLauncher {

    public static void main(String[] args) throws IOException {
        System.setProperty("log4j.configuration", "log4j_server.properties");
        Logger Log = LoggerFactory.getLogger(ServerLauncher.class);
        InputStream input = ServerLauncher.class.getResourceAsStream("/version.txt");
        String version = IOUtils.readLines(input, StandardCharsets.UTF_8).get(0);
        input.close();
        try {
            GaineaServer server = new GaineaServer(version);
            server.appendCLI();
        } catch (Exception e) {
            Log.error("Failed to launch server!", e);
        }
    }

}

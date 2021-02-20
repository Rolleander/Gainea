package com.broll.gainea.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.broll.gainea.Gainea;
import com.broll.gainea.GaineaServer;
import com.esotericsoftware.minlog.Log;

public class ServerLauncher {
    public static void main(String[] arg) {
//		Log.INFO();
        Log.DEBUG();
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        //config.width=1920;
//		config.height=1080;
        config.width = 500;
        config.height = 900;
        new LwjglApplication(new GaineaServer(), config);
    }
}

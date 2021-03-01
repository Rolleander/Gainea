package com.broll.gainea.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.ui.screens.TestMapScreen;
import com.broll.gainea.client.ui.screens.TestScreen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DesktopTestLauncher {
    public static void main(String[] arg) {
        System.setProperty("log4j.configuration", "log4j_client.properties");
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        //config.width=1920;
//		config.height=1080;
        config.width = 1280;
        config.height = 900;
        ClientMapContainer.RENDER_DEBUG = true;
        new LwjglApplication(new Gainea(new TestMapScreen(), false), config);
    }
}

package com.broll.gainea.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.screens.TestBattleScreen;

public class DesktopTestLauncher {
    public static void main(String[] arg) {
        AudioPlayer.changeMusicVolume(0);
        AudioPlayer.changeSoundVolume(0.2);
        System.setProperty("log4j.configuration", "log4j_client.properties");
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        //config.width=1920;
//		config.height=1080;
        config.width = 1280;
        config.height = 900;
        new LwjglApplication(new Gainea(new TestBattleScreen(), false), config);
    }
}

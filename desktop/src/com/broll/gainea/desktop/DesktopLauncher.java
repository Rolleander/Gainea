package com.broll.gainea.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.screens.StartScreen;

public class DesktopLauncher {

    public static void main(String[] arg) {
        System.setProperty("log4j.configuration", "log4j_client.properties");
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        if (arg.length > 0 && "test".equals(arg[0])) {
            StartScreen.PLAYER_NAME = "tester";
            StartScreen.SERVER = "localhost";
            AudioPlayer.changeMusicVolume(0.15);
            AudioPlayer.changeSoundVolume(0.3f);
        }
        config.setWindowedMode(1280, 900);
        config.setResizable(true);
        config.setWindowIcon("ui/icon_large.png", "ui/icon_small.png");
        new Lwjgl3Application(new Gainea(), config);
    }

}

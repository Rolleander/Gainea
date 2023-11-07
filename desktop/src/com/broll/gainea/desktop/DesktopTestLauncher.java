package com.broll.gainea.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.AudioPlayer;
import com.broll.gainea.client.ui.screens.StartScreen;
import com.broll.gainea.client.ui.screens.TestBattleScreen;

public class DesktopTestLauncher {
    public static void main(String[] arg) {
        AudioPlayer.changeMusicVolume(0);
        AudioPlayer.changeSoundVolume(0.3);
        System.setProperty("log4j.configuration", "log4j_client.properties");
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        if (arg.length > 0 && "test".equals(arg[0])) {
            StartScreen.PLAYER_NAME = "tester";
            StartScreen.SERVER = "localhost";
            AudioPlayer.changeMusicVolume(0);
            AudioPlayer.changeSoundVolume(0);
        }
        config.setWindowedMode(1280, 900);
        config.setResizable(true);
        config.setWindowIcon("ui/icon_large.png", "ui/icon_small.png");
        new Lwjgl3Application(new Gainea(new TestBattleScreen(), false), config);
    }
}

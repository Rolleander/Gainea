package com.broll.gainea.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.broll.gainea.Gainea;
import com.esotericsoftware.minlog.Log;

import static com.esotericsoftware.minlog.Log.LEVEL_TRACE;

public class DesktopLauncher {
	public static void main (String[] arg) {
		Log.INFO();
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		//config.width=1920;
//		config.height=1080;
		config.width=920;
		config.height=680;

		new LwjglApplication(new Gainea(), config);
	}
}

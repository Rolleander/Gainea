package com.broll.gainea;

import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.broll.gainea.server.init.NetworkSetup;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AndroidLauncher extends AndroidApplication {

	private final static  String NETWORK_CLASSES_FILE = "network_classes";  // Drop the filename extension, if any

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadNetClasses();
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer=false;
		config.useCompass=false;
		config.useGyroscope=false;
		config.useImmersiveMode=true;
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		initialize(new Gainea(), config);
	}

	private void loadNetClasses(){
		List<Class> classes = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(
				getResources().openRawResource(
						getResources().getIdentifier(
								NETWORK_CLASSES_FILE, "raw", getPackageName()))))) {
			for (String line; (line = reader.readLine()) != null; ) {
				classes.add( Class.forName(line));
			}
		} catch (Exception e) {
			throw new RuntimeException("Failed to load netclasses",e);
		}
		classes.sort(Comparator.comparing(Class::getName));
		NetworkSetup.NETWORK_PACKAGES.addAll(classes);
	}

}

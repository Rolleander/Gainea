package com.broll.gainea.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.broll.gainea.Gainea;
import com.broll.mpnll.client.NativeClientRegistry;
import com.broll.mpnll.gwt.MpnllWebsocketClient;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                GwtApplicationConfiguration config = new GwtApplicationConfiguration(true);
                config.padHorizontal = 0;
                config.padVertical = 0;
                return config;
        }

        @Override
        public ApplicationListener createApplicationListener () {
                NativeClientRegistry.NATIVE_CLIENT_FACTORY = MpnllWebsocketClient::new;
                return new Gainea();
        }
}

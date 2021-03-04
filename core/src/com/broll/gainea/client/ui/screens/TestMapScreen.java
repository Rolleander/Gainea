package com.broll.gainea.client.ui.screens;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.broll.gainea.Gainea;
import com.broll.gainea.client.game.ClientMapContainer;
import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.network.sites.GameEventSite;
import com.broll.gainea.client.ui.AbstractScreen;
import com.broll.gainea.net.NT_Action;
import com.broll.gainea.net.NT_Action_Attack;
import com.broll.gainea.net.NT_Action_Move;
import com.broll.gainea.net.NT_Action_PlaceUnit;
import com.broll.gainea.net.NT_BoardObject;
import com.broll.gainea.net.NT_BoardUpdate;
import com.broll.gainea.net.NT_Card;
import com.broll.gainea.net.NT_Event_MovedObject;
import com.broll.gainea.net.NT_Goal;
import com.broll.gainea.net.NT_Monster;
import com.broll.gainea.net.NT_Player;
import com.broll.gainea.net.NT_Unit;
import com.broll.gainea.server.core.map.Location;
import com.broll.gainea.server.core.map.impl.GaineaMap;
import com.broll.gainea.server.init.ExpansionSetting;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TestMapScreen extends AbstractScreen {

    public TestMapScreen() {
    }


    @Override
    public Actor build() {
//        ClientMapContainer.RENDER_DEBUG = true;
        game.state.init(ExpansionSetting.PLUS_ICELANDS, null);
        game.ui.initInGameUi();
        game.ui.inGameUI.show();
        game.state.getMap().displayRenders();
        return new Table();
    }
}

package com.broll.gainea.server.sites;

import com.broll.gainea.misc.EnumUtils;
import com.broll.gainea.net.NT_LobbySettings;
import com.broll.gainea.net.NT_PlayerChangeFraction;
import com.broll.gainea.net.NT_PlayerReady;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.LobbyFactory;
import com.broll.gainea.server.init.PlayerData;
import com.broll.gainea.server.core.fractions.FractionType;
import com.broll.gainea.server.core.map.ExpansionType;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.LobbyServerSite;
import com.broll.networklib.server.ConnectionRestriction;
import com.broll.networklib.server.RestrictionType;
import com.broll.networklib.server.impl.ILobbyCreationRequest;
import com.broll.networklib.server.impl.LobbyHandler;
import com.broll.networklib.server.impl.Player;
import com.broll.networklib.server.impl.ServerLobby;

public class GameLobbySite extends LobbyServerSite<LobbyData, PlayerData> {

    @Override
    public void init(LobbyGameServer<LobbyData, PlayerData> server, LobbyHandler<LobbyData, PlayerData> lobbyHandler) {
        super.init(server,lobbyHandler);
        this.lobbyHandler.setLobbyCreationRequestHandler(new ILobbyCreationRequest<LobbyData, PlayerData>() {
            @Override
            public ServerLobby<LobbyData, PlayerData> createNewLobby(Player<PlayerData> requester, String lobbyName, Object settings) {
                if (settings instanceof NT_LobbySettings) {
                    int expansion = ((NT_LobbySettings) settings).expansionSetting;
                    if (EnumUtils.inBounds(expansion, ExpansionType.class)) {
                        ServerLobby<LobbyData, PlayerData> lobby = lobbyHandler.openLobby(lobbyName);
                        LobbyFactory.initLobby(lobby, ExpansionSetting.values()[expansion]);
                        return lobby;
                    }
                }
                return null;
            }
        });
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_UNLOCKED)
    public void changeFraction(NT_PlayerChangeFraction change) {
        int newFraction = change.fraction;
        if (EnumUtils.inBounds(newFraction, FractionType.class)) {
            FractionType fraction = FractionType.values()[newFraction];
            //check if its free
            if (!getLobby().streamData().map(PlayerData::getFraction).filter(it -> it == fraction).findFirst().isPresent()) {
                //update player fraction
                getPlayer().getData().setFraction(fraction);
                getLobby().sendLobbyUpdate();
            }
        }
    }

    @PackageReceiver
    @ConnectionRestriction(RestrictionType.LOBBY_UNLOCKED)
    public void ready(NT_PlayerReady ready) {
        getPlayer().getData().setReady(ready.ready);
        getLobby().sendLobbyUpdate();
        //check for all ready, then lock lobby and start game
        ServerLobby<LobbyData, PlayerData> lobby = getLobby();
        synchronized (lobby) {
            if (lobby.streamData().map(PlayerData::isReady).reduce(true, Boolean::logicalAnd)) {
                lobby.setLocked(true);
                accessSite(GameStartSite.class).startGame();
            }
        }
    }

}

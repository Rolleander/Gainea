package com.broll.gainea.test;

import com.broll.gainea.net.NT_LobbySettings;
import com.broll.gainea.server.ExpansionSetting;
import com.broll.gainea.server.LobbyData;
import com.broll.gainea.server.NetworkSetup;
import com.broll.gainea.server.PlayerData;
import com.broll.networklib.NetworkRegister;
import com.broll.networklib.client.LobbyGameClient;
import com.broll.networklib.client.impl.GameLobby;
import com.broll.networklib.client.impl.ILobbyDiscovery;
import com.broll.networklib.network.nt.NT_LobbyInformation;
import com.broll.networklib.network.nt.NT_ServerInformation;
import com.broll.networklib.server.GameServer;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.impl.ServerLobby;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class KryoTest {

    @Test
    public void test() throws FileNotFoundException, ExecutionException, InterruptedException {
        LobbyGameServer<LobbyData, PlayerData> server = new LobbyGameServer<>("test", NetworkSetup::registerNetwork);
        NetworkSetup.setup(server);
        server.open();
        ServerLobby<LobbyData, PlayerData> lobby = server.getLobbyHandler().openLobby("testLobby");
        LobbyData ld = new LobbyData();
        ld.setExpansionSetting(ExpansionSetting.BASIC_GAME);
        lobby.setData(ld);
        LobbyGameClient client = new LobbyGameClient(NetworkSetup::registerNetwork);
        client.listLobbies("localhost", new ILobbyDiscovery() {
            @Override
            public void discovered(String serverIp, String serverName, List<GameLobby> lobbies) {
                assertEquals("test", serverName);
                NT_LobbySettings settings = (NT_LobbySettings)lobbies.get(0).getSettings();
                assertEquals(0,settings.expansionSetting);
            }

            @Override
            public void noLobbiesDiscovered() {
                fail();
            }

            @Override
            public void discoveryDone() {

            }
        }).get();

        //       kryo.register(NT_LobbyInformation.class);
        //     kryo.register(NT_LobbyInformation[].class);
        //     kryo.register(NT_ServerInformation.class);
        //    kryo.register(MyInfo.class);


        //    Output output = new Output(new FileOutputStream("file.bin"));
        //     kryo.writeObject(output, si);
        //     output.close();

        //    Input input = new Input(new FileInputStream("file.bin"));
        //     NT_ServerInformation<NT_LobbySettings> newsi = kryo.readObject(input, NT_ServerInformation.class);
        //    input.close();

        //   System.out.println(        newsi.lobbies[0].settings.expansionSetting);
        //   assertEquals(si.serverName, newsi.serverName);
    }


}

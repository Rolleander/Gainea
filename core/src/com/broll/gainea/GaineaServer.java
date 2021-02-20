package com.broll.gainea;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.broll.gainea.client.Assets;
import com.broll.gainea.client.game.ClientHandler;
import com.broll.gainea.client.game.GameState;
import com.broll.gainea.client.ui.GameUI;
import com.broll.gainea.server.init.ExpansionSetting;
import com.broll.gainea.server.init.LobbyData;
import com.broll.gainea.server.init.LobbyFactory;
import com.broll.gainea.server.init.NetworkSetup;
import com.broll.gainea.server.init.PlayerData;
import com.broll.networklib.PackageReceiver;
import com.broll.networklib.network.nt.NT_LobbyKicked;
import com.broll.networklib.server.LobbyGameServer;
import com.broll.networklib.server.LobbyServerCLI;
import com.broll.networklib.server.impl.BotSite;
import com.broll.networklib.server.impl.Player;
import com.broll.networklib.server.impl.ServerLobby;
import com.broll.networklib.server.impl.ServerLobbyListener;
import com.esotericsoftware.minlog.Log;

public class GaineaServer extends ApplicationAdapter {

    private  LobbyGameServer<LobbyData, PlayerData> server;

    public Stage uiStage;
    public Assets assets;
    public Skin skin;


    @Override
    public void create() {
        assets = new Assets(false);
        assets.getManager().finishLoading();
        skin = assets.get("ui/cloud-form-ui.json", Skin.class);
        uiStage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(uiStage);
        Table table = new Table();
        TextButton refresh = new TextButton("Refresh",skin);
        table.add(refresh).row();
        Tree<Node, String> tree = new Tree<>(skin);
        table.add(tree).row();
        table.setFillParent(true);
        table.pad(5);
        table.align(Align.topLeft);
        uiStage.addActor(table);
        refresh.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Log.info("Refresh");
                tree.clearChildren();
                server.getLobbyHandler().getLobbies().forEach(lobby-> tree.add(showLobby(lobby)));
                tree.expandAll();
            }
        });
    }

    private class Node extends Tree.Node<Node, String, Actor >{
        public Node(Actor content){
            super(content);
        }
    }

    private Node showLobby(ServerLobby<LobbyData, PlayerData> lobby){
        Node lob= new Node(new Label(lobby.getName(),skin));
        lob.add(new Node(new Label("test",skin)));
        lob.add(new Node(new Label("test2",skin)));
        return lob;
    }

    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        uiStage.act(Gdx.graphics.getDeltaTime());
        uiStage.draw();
    }

    @Override
    public void dispose() {
        assets.getManager().dispose();
        uiStage.dispose();
    }

    public static void main(String[] args) {
        GaineaServer server = new GaineaServer();
        server.appendCLI();
    }

    public GaineaServer(){
//        Log.set(Log.LEVEL_INFO);
        Log.DEBUG();
        server = new LobbyGameServer<>("GaineaServer", NetworkSetup::registerNetwork);
        NetworkSetup.setup(server);
        server.open();
        ServerLobby<LobbyData, PlayerData> lobby = server.getLobbyHandler().openLobby("Testlobby");
        LobbyFactory.initLobby(lobby, ExpansionSetting.BASIC_GAME);
        lobby.setAutoClose(false);
     /*   PlayerData data = new PlayerData();
        data.setReady(true);
        lobby.createBot("bot_hans", data).ifPresent(bot -> {
            bot.register(new BotSite<PlayerData>() {
                @PackageReceiver
                void rec(NT_LobbyKicked f) {

                }
            });
        });*/
    }

    public void appendCLI(){
        LobbyServerCLI.open(server);
    }

}

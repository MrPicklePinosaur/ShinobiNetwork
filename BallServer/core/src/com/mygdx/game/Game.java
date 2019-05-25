package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {

    //private World world;
    private CopyOnWriteArrayList<String> chat_log;
    private LinkedList<Player> player_list;

    World world;
    Map map;
    AssetManager assetmanager;

    public Game(Map map) {
        this.world = new World(new Vector2(0,0),true);
        this.world.setContactListener(new CollisionListener());

        this.map = map;
        this.assetmanager = new AssetManager(this);

        this.chat_log = new CopyOnWriteArrayList<String>();
        this.player_list = new LinkedList<Player>();
    }

    public void new_chat_msg(String msg) {
        chat_log.add(msg);
        this.update_chat(); //when a new message is added, update all clients
    }
    public void wipe_chat() { this.chat_log.clear(); }
    private void update_chat() {
        if (this.chat_log.size() == 0) { return; } //if there is nothing to send
        String chat = "";
        for (String s : this.chat_log) { chat+=(","+s); }
        chat = toString().substring(1);
        BallClientHandler.broadcast(MT.SENDCHAT,chat);
    }

    public ArrayList<Vector3> getLeaderBoard() {
        ArrayList<Vector3> leaderboard = new ArrayList<Vector3>();
        for (Player p : this.player_list) { leaderboard.add(p.getGameStats()); }
        return leaderboard;
    }

    public LinkedList<Player> getPlayerList() { return this.player_list; }
    public void addPlayer(Player p) { this.player_list.add(p); }
    public void removePlayer(Player p) {
        assert (this.player_list.contains(p)): "PLayer cannot be removed as it is not found";
        this.player_list.remove(p);
    }

    public void dispose() {
        this.world.dispose();
    }

    public World getWorld() { return this.world; }
    public Map getMap() { return this.map; }

}

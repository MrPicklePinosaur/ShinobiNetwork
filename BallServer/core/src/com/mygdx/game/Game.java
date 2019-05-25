package com.mygdx.game;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {

    private CopyOnWriteArrayList<Entity> entity_list = new CopyOnWriteArrayList<Entity>();  //just a simple list of all the alive entities
    private LinkedList<Player> player_list;
    private CopyOnWriteArrayList<String> chat_log;

    private World world;
    private Map map;
    private AssetManager assetManager;

    public Game(World world,Map map) {

        this.world = world;
        this.map = map;
        this.assetManager = new AssetManager(this.world);

        this.entity_list = new CopyOnWriteArrayList<Entity>();
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

    public String send_all() { //packages all entity positions into a string
        String msg = "";
        for (Entity e : this.entity_list) { //for each entity
            ET et = e.getET();
            float rot = e.getRotation();
            if (et == ET.PLAYER) { //if we are sending a player's data, send their mouse angle instead of rotation
                Player p = (Player) e;
                rot = p.getMouseAngle();
            }
            msg += (" "+e.getET().toString()+","+e.getId()+","+e.getTexturePath()+","+e.getX()+","+e.getY()+","+rot);
        }

        if (!msg.equals("")) { msg = msg.substring(1); } //get rid of extra space
        return msg;
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

    public void addEntity(Entity entity) { this.entity_list.add(entity); }
    public void removeEntity(Entity entity) {
        assert (this.entity_list.contains(entity)): "The entity that you are trying to remove isn't in the master list";
        this.entity_list.remove(entity);
        BallClientHandler.broadcast(MT.KILLENTITY,""+entity.getId()); //tell client to stop drawing it
    }

    public World getWorld() { return this.world; }
    public Map getMap() { return this.map; }
    public AssetManager getAssetManager() { return this.assetManager; }

}

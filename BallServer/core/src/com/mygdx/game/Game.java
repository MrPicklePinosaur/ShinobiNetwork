package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.World;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public class Game {

    //private World world;
    private CopyOnWriteArrayList<String> chat_log;
    private LinkedList<Player> player_list;

    public Game() {
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

}

package com.mygdx.game;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.mygdx.game.TEAMTAG.SOLO;

public class Game {

    private CopyOnWriteArrayList<String> chat_log;
    private LinkedList<Player> player_list;
    private GAMETYPE gametype;

    public Game(GAMETYPE gametype) {
        this.gametype = gametype;

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

    public TEAMTAG chooseTeam() {
        if (this.gametype == GAMETYPE.FFA) {
            return SOLO;
        } else if (this.gametype == GAMETYPE.TDM) { //team deathmatch
            //find how many players are on each team and try to keep the teams balanced
            int red_count = 0;
            int blue_count = 0;

            for (Player p : this.player_list) {
                if (p.getTeamtag() == TEAMTAG.RED) { red_count++; }
                else if (p.getTeamtag() == TEAMTAG.BLUE) { blue_count++; }
            }

            return red_count >= blue_count ? TEAMTAG.BLUE : TEAMTAG.RED;
        }
        return null;
    }

}

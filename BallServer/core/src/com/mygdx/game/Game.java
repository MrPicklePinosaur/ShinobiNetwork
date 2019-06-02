package com.mygdx.game;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

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
        if (msg.equals("") || msg == null) { return; }
        msg = "USER: " + msg;

        String text_colour = "grey";
        BallClientHandler.broadcast(MT.SENDCHAT,text_colour+","+msg);
    }
    public void wipe_chat() { this.chat_log.clear(); }

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
        TEAMTAG team = null;
        if (this.gametype == GAMETYPE.FFA) {
            team = TEAMTAG.SOLO;
        } else if (this.gametype == GAMETYPE.TDM) { //team deathmatch
            //find how many players are on each team and try to keep the teams balanced
            int red_count = 0;
            int blue_count = 0;

            for (Player p : this.player_list) {
                if (p.getTeamtag() == TEAMTAG.RED) { red_count++; }
                else if (p.getTeamtag() == TEAMTAG.BLUE) { blue_count++; }
            }

            team = red_count >= blue_count ? TEAMTAG.BLUE : TEAMTAG.RED;
        }

        assert (team != null): "Error choosing team";
        return team;
    }

    /*
    public String getTextColour(TEAMTAG teamtag) {
        String text_colour;

    }
    */

}

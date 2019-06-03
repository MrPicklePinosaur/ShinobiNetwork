package com.mygdx.game;

import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Game {

    protected CopyOnWriteArrayList<String> chat_log;
    protected LinkedList<Player> player_list;

    public Game() {
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

    public abstract ArrayList<Vector3> getLeaderBoard();

    public LinkedList<Player> getPlayerList() { return this.player_list; }
    public void addPlayer(Player p) { this.player_list.add(p); }
    public void removePlayer(Player p) {
        assert (this.player_list.contains(p)): "PLayer cannot be removed as it is not found";
        this.player_list.remove(p);
    }

    public abstract TEAMTAG chooseTeam();

    /*
    public String getTextColour(TEAMTAG teamtag) {
        String text_colour;

    }
    */

}

class TDMGame extends Game {

    public TDMGame() {

    }

    @Override public ArrayList<Vector3> getLeaderBoard() {
        ArrayList<Vector3> leaderboard = new ArrayList<Vector3>();
        for (Player p : this.player_list) { leaderboard.add(p.getGameStats()); }
        return leaderboard;
    }

    @Override public TEAMTAG chooseTeam() {
        //find how many players are on each team and try to keep the teams balanced
        int red_count = 0;
        int blue_count = 0;

        for (Player p : this.player_list) {
            if (p.getTeamtag() == TEAMTAG.RED) { red_count++; }
            else if (p.getTeamtag() == TEAMTAG.BLUE) { blue_count++; }
        }

        return red_count >= blue_count ? TEAMTAG.BLUE : TEAMTAG.RED;
    }
}

class FFAGame extends Game {

    public FFAGame() {

    }

    @Override public ArrayList<Vector3> getLeaderBoard() {
        ArrayList<Vector3> leaderboard = new ArrayList<Vector3>();
        for (Player p : this.player_list) { leaderboard.add(p.getGameStats()); }
        return leaderboard;
    }

    @Override public TEAMTAG chooseTeam() {
        return TEAMTAG.SOLO;
    }

}

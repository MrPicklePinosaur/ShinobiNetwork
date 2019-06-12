package com.mygdx.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Game {

    protected CopyOnWriteArrayList<String> chat_log;
    protected CopyOnWriteArrayList<Player> player_list;

    public Game() {
        this.chat_log = new CopyOnWriteArrayList<String>();
        this.player_list = new CopyOnWriteArrayList<Player>();
    }

    public void new_chat_msg(String msg) {
        chat_log.add(msg);
        if (msg.equals("") || msg == null) { return; }

        String text_colour = "grey";
        BallClientHandler.broadcast(MT.SENDCHAT,text_colour+","+msg);
    }
    public void wipe_chat() { this.chat_log.clear(); }

    public CopyOnWriteArrayList<Player> getPlayerList() { return this.player_list; }
    public void addPlayer(Player p) { this.player_list.add(p); }
    public void removePlayer(Player p) {
        assert (this.player_list.contains(p)): "PLayer cannot be removed as it is not found";
        this.player_list.remove(p);
    }

    public boolean checkObjective(String zonename) {
        for (Player p : this.player_list) {
            for (Map.Entry<String, Rectangle> entry : Global.map.getObjectives().entrySet()) {
                String name = entry.getKey();
                Rectangle zoneRect = entry.getValue();

                if (!zonename.equals(name)) { continue; }
                if (zoneRect.contains(p.getX(),p.getY())) { return true; }
            }
        }
        return false;
    }

    public boolean isInsideSpawn(Player p) {
        if ((Global.game.checkObjective("red_spawn") && p.getTeamtag() == TEAMTAG.RED) || (Global.game.checkObjective("blue_spawn") && p.getTeamtag() == TEAMTAG.BLUE)) {
            return true;
        } return false;
    }

    public String getAllHP() {
        String msg = "";
        for (Player p : this.player_list) {
            msg += (" "+p.getId()+","+p.getCurrentHp());
        }
        if (msg.length() != 0) { msg = msg.substring(1); }
        return msg;
    }


    public abstract ArrayList<Vector3> getLeaderBoard();
    public abstract TEAMTAG chooseTeam();
    public abstract void addKill(Player player);

    /*
    public String getTextColour(TEAMTAG teamtag) {
        String text_colour;

    }
    */

}

class TDMGame extends Game { //team death match

    private static int MAX_LIVES = 20;
    private int red_lives = MAX_LIVES;
    private int blue_lives = MAX_LIVES;

    public TDMGame() {

    }

    @Override public void addKill(Player player) { //called when someone dies
        if (player.getTeamtag() == TEAMTAG.RED) { //if the player that got the kill is a member of red, take a life off of blue
            blue_lives--;
            this.new_chat_msg("A member of the BLUE TEAM has been slain!!!");
            this.checkWin();
        }
        else if (player.getTeamtag() == TEAMTAG.BLUE) {
            red_lives--;
            this.new_chat_msg("A member of the RED TEAM has been slain!!!");
            this.checkWin();
        }
     }

     public void checkWin() {
         if (blue_lives <= 0) { //BLUE LOSES!
             this.new_chat_msg("The RED TEAM emerges VICTORIOUS");
         } else if (red_lives <= 0) { //RED LOSES!
             this.new_chat_msg("The BLUE TEAM emerges VICTORIOUS");
         }
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

class KOTHGame extends Game { //king of the hill

    private static float POINTS_TO_WIN = 100;
    private static float POINTS_PER_TICK = 0.05f;
    private static float POINTS_UPON_KILL = 5;

    private float red_points = 0;
    private float blue_points = 0;

    public KOTHGame() {

    }

    public void insideZone(TEAMTAG teamtag) {
        if (teamtag == TEAMTAG.RED) {
            red_points+=POINTS_PER_TICK;
            this.checkWin();
        } else if (teamtag == TEAMTAG.BLUE) {
            blue_points+=POINTS_PER_TICK;
            this.checkWin();
        }
    }

    @Override public void addKill(Player player) {
        if (player.getTeamtag() == TEAMTAG.RED) { //if the player that got the kill is a member of red, red gets bonus points
            red_points+=POINTS_UPON_KILL;
            this.new_chat_msg("A member of the BLUE TEAM has been slain!!!");
            this.checkWin();
        }
        else if (player.getTeamtag() == TEAMTAG.BLUE) {
            blue_points+=POINTS_UPON_KILL;
            this.new_chat_msg("A member of the RED TEAM has been slain!!!");
            this.checkWin();
        }
    }

    public void checkWin() {
        if (red_points >= POINTS_TO_WIN) {
            this.new_chat_msg("The RED TEAM emerges VICTORIOUS");
            //TODO: win handling
        } else if (blue_points >= POINTS_TO_WIN) {
            this.new_chat_msg("The BLUE TEAM emerges VICTORIOUS");
        }
    }

    @Override public ArrayList<Vector3> getLeaderBoard() { //TODO: when sending team points, round to nearest whole number
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

class FFAGame extends Game { //free for all

    private static int KILLS_TO_WIN = 7;

    public FFAGame() {

    }

    @Override public void addKill(Player player) {
        this.new_chat_msg("USER has obtained a KILL!");
        if (player.getKills() >= KILLS_TO_WIN) { //Player wins
            this.new_chat_msg("USER has emerged VICTORIOUS");
        }
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

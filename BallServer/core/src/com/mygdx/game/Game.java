/* Shinobi Network
 ______   ______   __    __   ______
/\  ___\ /\  __ \ /\ "-./  \ /\  ___\
\ \ \__ \\ \  __ \\ \ \-./\ \\ \  __\
 \ \_____\\ \_\ \_\\ \_\ \ \_\\ \_____\
  \/_____/ \/_/\/_/ \/_/  \/_/ \/_____/

    Handles game logic and the game loop
    Also stores info about each player
    Sublclasses of this class are just differernt gamemodes that require different logic
 */

package com.mygdx.game;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Game {

    protected CopyOnWriteArrayList<Player> player_list; //list of all players  in game
    protected CopyOnWriteArrayList<String> chat_log; //list of all the chat messages that were sent

    public Game() {
        this.player_list = new CopyOnWriteArrayList<Player>();
        this.chat_log = new CopyOnWriteArrayList<String>();
    }

    public void new_chat_msg(String msg) { //add a new mesages to chat
        chat_log.add(msg);
        if (msg.equals("") || msg == null) { return; }

        String text_colour = "grey";
        BallClientHandler.broadcast(MT.SENDCHAT,text_colour+","+msg);
    }
    public void wipe_chat() { this.chat_log.clear(); }

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

    //getters
    public CopyOnWriteArrayList<Player> getPlayerList() { return this.player_list; }
    public abstract String getLeaderBoard();
    public boolean isInsideSpawn(Player p) {
        if ((Global.game.checkObjective("red_spawn") && p.getTeamtag() == TEAMTAG.RED) || (Global.game.checkObjective("blue_spawn") && p.getTeamtag() == TEAMTAG.BLUE)) {
            return true;
        } return false;
    }
    public String getAllHP() {
        String msg = "";
        for (Player p : this.player_list) {
            float hp = p.getCurrentHp()/p.stats.getHp();
            msg += (" "+p.getId()+","+hp);
        }
        if (msg.length() != 0) { msg = msg.substring(1); }
        return msg;
    }

    //setters
    public void addPlayer(Player p) { this.player_list.add(p); }
    public void removePlayer(Player p) {
        assert (this.player_list.contains(p)): "PLayer cannot be removed as it is not found";
        this.player_list.remove(p);
    }


    public abstract TEAMTAG chooseTeam();
    public abstract void addKill(Player player);

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
             BallClientHandler.broadcast(MT.GAMEOVER,"RED");
         } else if (red_lives <= 0) { //RED LOSES!
             this.new_chat_msg("The BLUE TEAM emerges VICTORIOUS");
             BallClientHandler.broadcast(MT.GAMEOVER,"BLUE");
         }
     }

    @Override public String getLeaderBoard() {
        return "";
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

    public void insideZone(TEAMTAG teamtag) { //check to see if a player is inside an objective zone
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

    @Override public String getLeaderBoard() { //TODO: when sending team points, round to nearest whole number
        return "";
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

    @Override public void addKill(Player player) { //if a player gets a kill
        this.new_chat_msg("["+player.getUserName()+"] has obtained a KILL!");
        if (player.getKills() >= KILLS_TO_WIN) { //Player wins
            this.new_chat_msg("["+player.getUserName()+"] has emerged VICTORIOUS");
        }
    }

    @Override public String getLeaderBoard() { //package the perofrmance stats of all players
        String leaderboard = "";
        for (Player p : this.player_list) {
             leaderboard += (" "+p.getUserName()+","+p.getKills()+","+p.getDeaths()+","+p.getDmgDealt());
        }
        if (leaderboard.length() != 0) { leaderboard = leaderboard.substring(1); }
        return leaderboard;
    }

    @Override public TEAMTAG chooseTeam() {
        return TEAMTAG.SOLO;
    }

}

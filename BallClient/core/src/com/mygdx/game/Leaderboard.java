/* Shinobi Network
 __       ______   ______   _____    ______   ______   ______   ______   ______   ______   _____
/\ \     /\  ___\ /\  __ \ /\  __-. /\  ___\ /\  == \ /\  == \ /\  __ \ /\  __ \ /\  == \ /\  __-.
\ \ \____\ \  __\ \ \  __ \\ \ \/\ \\ \  __\ \ \  __< \ \  __< \ \ \/\ \\ \  __ \\ \  __< \ \ \/\ \
 \ \_____\\ \_____\\ \_\ \_\\ \____- \ \_____\\ \_\ \_\\ \_____\\ \_____\\ \_\ \_\\ \_\ \_\\ \____-
  \/_____/ \/_____/ \/_/\/_/ \/____/  \/_____/ \/_/ /_/ \/_____/ \/_____/ \/_/\/_/ \/_/ /_/ \/____/


Leaderboard class:
This class is responsible for displaying a leaderboard ranking all the players by kills,
with a differently formatted leaderboard depending on the type of gamemode.
Thus, there is a general update method (updateLeaderboard()), and that
calls the appropriate update method based on the gamemode (either updateFFA()
or updateTeam()).

Methods (that aren't setters/getters):
    updateLeaderboard:
        This method is a general update method that takes in the necessary data relating to all players,
        reformats it so it can be more readable/understandably sorted in the more specific update methods
    updateFFA:
        This method is just a simple leaderboard. There aren't any teams, so there is simply a board of
        players and their associated data, sorted by most kills
    updateTeam:
        In this method, the gamemode states that there are two teams. Therefore, each leaderboard is split
        up into two teams, and the two teams are ranked in terms of kills independently of each other. Each
        team is also sorted by most kills
*/

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

import java.util.*;

public class Leaderboard {
    //Leaderboard keeps track of 4 things:
    //Kills, Deaths, Damage Dealt, and the associated Username
    static Table table; //table that is used to logically organize data for leaderboard
    static TextureAtlas atlas;
    static Skin skin;
    private String gamemode; //what the general update method uses to decide what to display
    private Table redTable; //team tables that are only used during team gamemode
    private Table blueTable;
    private Label name;
    private Label kills;
    private Label deaths;
    private Label damage;
    private Cell redTeam;   //team cells (what columns in each row of a table is) for team gamemode
    private Cell blueTeam;
    private LinkedList<Integer> numberData = new LinkedList<Integer>();
    private HashMap<String, LinkedList<Integer>> leaderboardData = new LinkedHashMap<String, LinkedList<Integer>>();
    static{
        atlas = new TextureAtlas("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.atlas");
        skin = new Skin(Gdx.files.internal("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.json"));
        skin.addRegions(atlas);
        table = new Table();
        table.setPosition(Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f);
        //table.setDebug(true); - used to debug leaderboard
        table.center(); //centers the table's contents on the screen
    }
    public Leaderboard(String gamemode){    //constructor that creates basic leaderboard resources based on gamemode
        this.gamemode = gamemode;
        if(this.gamemode.equals("Team")){
            Pixmap redTitlePixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888); //pixel used to set color of red team leaderboard
            redTitlePixmap.setColor(Color.valueOf("db0a3180"));//hexcode with A - 80: 50% transparency
            redTitlePixmap.fill();
            Table redTeamTitle = new Table(skin);
            redTeamTitle.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(redTitlePixmap))));  //changing color of red team's leaderboard header to red

            /*
            Cheatsheet:
            100% — FF
            95% — F2
            90% — E6
            85% — D9
            80% — CC
            75% — BF
            70% — B3
            65% — A6
            60% — 99
            55% — 8C
            50% — 80
            45% — 73
            40% — 66
            35% — 59
            30% — 4D
            25% — 40
            20% — 33
            15% — 26
            10% — 1A
            5% — 0D
            0% — 00
            */

            //www.hexcolortool.com for color codes

            //change blue team's leaderboard's header background color to blue
            Pixmap blueTitlePixmap = new Pixmap(1,1,Pixmap.Format.RGB565);
            blueTitlePixmap.setColor(Color.valueOf("084ca180"));
            blueTitlePixmap.fill();
            Table blueTeamTitle = new Table(skin);
            blueTeamTitle.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(blueTitlePixmap))));

            redTeamTitle.add(new Label("Red Team",skin));   //header for red team
            blueTeamTitle.add(new Label("Blue Team",skin)); //header for blue team

            //adding headers to the team leaderboards
            table.add(redTeamTitle).right().padRight(10f);
            table.add(blueTeamTitle).left().padLeft(10f);
            table.row();

            //creating the team leaderboards that will be under the team headers
            redTable = new Table(skin);
            blueTable = new Table(skin);

            //set team leaderboard colours
            Pixmap redPixmap = new Pixmap(1,1,Pixmap.Format.RGB565);
            redPixmap.setColor(Color.valueOf("7a0606BF"));
            redPixmap.fill();
            redTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(redPixmap))));
            Pixmap bluePixmap = new Pixmap(1,1,Pixmap.Format.RGB565);
            bluePixmap.setColor(Color.valueOf("06137aBF"));
            bluePixmap.fill();
            blueTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(bluePixmap))));

            //adding team leaderboard beneath headers
            redTeam = table.add(redTable).width(500f).top();
            blueTeam = table.add(blueTable).width(500f).top();

            redPixmap.dispose();
            bluePixmap.dispose();
            //redTable.setDebug(true);
            //blueTable.setDebug(true);
        }
    }
    public void updateLeaderboard(String[] unformattedData){
        //generic update method that takes in data
        //and sends it to the more specific update
        //methods in a more comprehensible way
        for(String data : unformattedData){ //look at each individual player separately
            String[] info = data.split(",");    //separate each player's info
            //get their kills, deaths, and damage dealt (in that order)
            numberData.add(Integer.parseInt(info[1]));
            numberData.add(Integer.parseInt(info[2]));
            numberData.add(Integer.parseInt(info[3]));
            leaderboardData.put(info[0],numberData);    //associate the player's name with the player's data
            numberData.clear();
        }

        //call specfied gamemode's unique update method
        if(this.gamemode.equals("FFA")){
            updateFFA(leaderboardData);
        }
        if(this.gamemode.equals("Team")){
            updateTeam(leaderboardData);
            redTeam.fill();
            blueTeam.fill();
        }
    }
    private void updateFFA(HashMap<String, LinkedList<Integer>> leaderboardData){
        table.clearChildren();
        int i = 0;
        LinkedList<String> sortedLeaderboard = new LinkedList<String>();    //list of players for leaderboard sorted by most kills
        for(String player : leaderboardData.keySet()){
            //for each player playing the game currently,
            //add the kills the player has in a string along with the player's name to a list
            //this means that we can sort the entries in the list by the number of kills,
            //while still associating it with the player's name, without using a map
            sortedLeaderboard.add(leaderboardData.get(player).get(0)+" "+player);
        }
        //sort the players by most kills
        Collections.sort(sortedLeaderboard,Collections.<String>reverseOrder());
        for(String data : sortedLeaderboard){   //for each player, starting from the one with the most kills
            String player = data.split(" ")[1]; //get the player's username
            i++;
            if(i%2!=0){ //alternate colouring
                name = new Label(player,skin);
                kills = new Label(""+leaderboardData.get(player).get(0),skin);  //get how many kills the player has
                deaths = new Label(""+leaderboardData.get(player).get(1),skin); //get how many times the player has died
                damage = new Label(""+leaderboardData.get(player).get(2),skin); //get how much damage the player has done
            }else {
                name = new Label(player, skin);
                kills = new Label("" + leaderboardData.get(player).get(0), skin);
                deaths = new Label("" + leaderboardData.get(player).get(1), skin);
                damage = new Label("" + leaderboardData.get(player).get(2), skin);
            }

            //in case the names, # of kills/deaths, or damage done
            //gets too large to fit flush with the other players,
            //just wrap the text in the problematic column
            name.setWrap(true);
            kills.setWrap(true);
            deaths.setWrap(true);
            damage.setWrap(true);

            //add each player to the leaderboard
            table.add(name).width(100f);
            table.add(kills).width(100f);
            table.add(deaths).width(100f);
            table.add(damage).width(100f);
            table.row();
        }
    }
    private void updateTeam(HashMap<String, LinkedList<Integer>> leaderboardData){
        //refresh team data
        redTable.clearChildren();
        blueTable.clearChildren();

        int i = 0;  //later on, we plan on alternating each player's colour in the leaderboard
        // 0 = darker colour
        // 1 = lighter colour

        //listed of usernames (+ associated kills) per team, that will be sorted by kills later
        LinkedList<String> sortedRed = new LinkedList<String>();
        LinkedList<String> sortedBlue = new LinkedList<String>();
        int emptySlots=6;   //if we plan on changing how many max players there can be later dynamically, we would need
                            //to keep that in mind while displaying leaderboard
        for(String player : leaderboardData.keySet()){
            if(leaderboardData.get(player).get(3)==0){ //red player
                //since we are sorting players by kills, we first
                //create lists where each entry starts with the number of kills,
                //and then is followed by the username of the player with that many kills
                //this way, when we sort these lists, we will also sort the players as well
                sortedRed.add(leaderboardData.get(player).get(0)+" "+player);
            }else{  //blue player
                sortedBlue.add(leaderboardData.get(player).get(0)+" "+player);
            }
        }
        emptySlots = Math.max(6-sortedRed.size(),6-sortedBlue.size());  //for changing team size dynamically in the future

        //sorting the players
        Collections.sort(sortedRed,Collections.<String>reverseOrder());
        Collections.sort(sortedBlue,Collections.<String>reverseOrder());
        for(String data : sortedRed){   //for each player on the red team (descending in kills)
            String player = data.split(" ")[1]; //get the player's username
            i++;
            if(i%2!=0){ //alternate colouring
                name = new Label(player,skin);
                name.setWrap(true);
                kills = new Label(""+leaderboardData.get(player).get(0),skin);  //get how many people the player has killed
                kills.setAlignment(Align.right);
                deaths = new Label(""+leaderboardData.get(player).get(1),skin); //get how many times the player has died
                deaths.setAlignment(Align.right);
                damage = new Label(""+leaderboardData.get(player).get(2),skin); //get how much damage the player has caused overall
                damage.setAlignment(Align.right);
            }else {
                name = new Label(player, skin);
                kills = new Label("" + leaderboardData.get(player).get(0), skin);
                kills.setAlignment(Align.right);
                deaths = new Label("" + leaderboardData.get(player).get(1), skin);
                deaths.setAlignment(Align.right);
                damage = new Label("" + leaderboardData.get(player).get(2), skin);
                damage.setAlignment(Align.right);
            }
            //in case the names, # of kills/deaths, or damage done
            //gets too large to fit flush with the other players,
            //just wrap the text in the problematic column
            name.setWrap(true);
            kills.setWrap(true);
            deaths.setWrap(true);
            damage.setWrap(true);

            //add each player to the red team leaderboard
            redTable.add(name).width(100f).padBottom(5f).padTop(5f);
            redTable.add(kills).width(50f);
            redTable.add(deaths).width(50f);
            redTable.add(damage).width(50f);
            redTable.row();
        }
        for(String data : sortedBlue){
            //repeat the same process as above, but for the blue team
            String player = data.split(" ")[1];
            i++;
            if(i%2!=0){ //alternate colouring
                name = new Label(player,skin);
                kills = new Label(""+leaderboardData.get(player).get(0),skin);
                kills.setAlignment(Align.right);
                deaths = new Label(""+leaderboardData.get(player).get(1),skin);
                deaths.setAlignment(Align.right);
                damage = new Label(""+leaderboardData.get(player).get(2),skin);
                damage.setAlignment(Align.right);
            }else {
                name = new Label(player, skin);
                kills = new Label("" + leaderboardData.get(player).get(0), skin);
                kills.setAlignment(Align.right);
                deaths = new Label("" + leaderboardData.get(player).get(1), skin);
                deaths.setAlignment(Align.right);
                damage = new Label("" + leaderboardData.get(player).get(2), skin);
                damage.setAlignment(Align.right);
            }

            name.setWrap(true);
            /*unlikely that
            kills, deaths, or
            damage will need to
            wrap, but better safe than sorry*/
            kills.setWrap(true);
            deaths.setWrap(true);
            damage.setWrap(true);


            blueTable.add(name).width(100f).padBottom(5f).padTop(5f);
            blueTable.add(kills).width(50f);
            blueTable.add(deaths).width(50f);
            blueTable.add(damage).width(50f);
            blueTable.row();
        }
    }
}

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
    static Window leaderboard;
    static Table table;
    static TextureAtlas atlas;
    static Skin skin;
    private String gamemode;
    private Table redTable;
    private Table blueTable;
    private Label name;
    private Label kills;
    private Label deaths;
    private Label damage;
    private Cell redTeam;
    private Cell blueTeam;
    private LinkedList<Integer> numberData = new LinkedList<Integer>();
    private HashMap<String, LinkedList<Integer>> leaderboardData = new LinkedHashMap<String, LinkedList<Integer>>();
    static{
        atlas = new TextureAtlas("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.atlas");
        skin = new Skin(Gdx.files.internal("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.json"));
        skin.addRegions(atlas);
        leaderboard = new Window("leaderboard",skin);
        table = new Table();
        table.setPosition(Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()/2f);
        table.setDebug(true);
        //table.setFillParent(true);
        table.center();
    }
    public Leaderboard(String gamemode){
        this.gamemode = gamemode;
        if(this.gamemode.equals("Team")){
            //221, 95, 116;
            Pixmap redTitlePixmap = new Pixmap(1,1,Pixmap.Format.RGBA8888);
            redTitlePixmap.setColor(Color.valueOf("db0a3180"));//hexcode with A - 80: 50% transparency
            redTitlePixmap.fill();
            Table redTeamTitle = new Table(skin);
            redTeamTitle.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(redTitlePixmap))));

            /*
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

            Pixmap blueTitlePixmap = new Pixmap(1,1,Pixmap.Format.RGB565);
            blueTitlePixmap.setColor(Color.valueOf("084ca180"));
            blueTitlePixmap.fill();
            Table blueTeamTitle = new Table(skin);
            blueTeamTitle.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(blueTitlePixmap))));

            redTeamTitle.add(new Label("Red Team",skin));
            blueTeamTitle.add(new Label("Blue Team",skin));
            table.add(redTeamTitle).right().padRight(10f);
            table.add(blueTeamTitle).left().padLeft(10f);
            table.row();
            redTable = new Table(skin);
            blueTable = new Table(skin);
            Pixmap redPixmap = new Pixmap(1,1,Pixmap.Format.RGB565);
            redPixmap.setColor(Color.valueOf("7a0606BF"));
            redPixmap.fill();
            redTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(redPixmap))));
            Pixmap bluePixmap = new Pixmap(1,1,Pixmap.Format.RGB565);
            bluePixmap.setColor(Color.valueOf("06137aBF"));
            bluePixmap.fill();
            blueTable.setBackground(new TextureRegionDrawable(new TextureRegion(new Texture(bluePixmap))));
            redTeam = table.add(redTable).width(500f).top();
            blueTeam = table.add(blueTable).width(500f).top();

            redPixmap.dispose();
            bluePixmap.dispose();
            //redTable.setDebug(true);
            //blueTable.setDebug(true);
        }
    }
    public void updateLeaderboard(String[] unformattedData){
        for(String data : unformattedData){
            String[] info = data.split(",");
            numberData.add(Integer.parseInt(info[1]));
            numberData.add(Integer.parseInt(info[2]));
            numberData.add(Integer.parseInt(info[3]));
            leaderboardData.put(info[0],numberData);
            numberData.clear();
        }
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
        /*Pixmap pm1 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm1.setColor(Color.GREEN);
        pm1.fill();

        Pixmap pm2 = new Pixmap(1, 1, Pixmap.Format.RGB565);
        pm2.setColor(Color.RED);
        pm2.fill();*/
        table.clearChildren();
        int i = 0;
        LinkedList<String> sortedLeaderboard = new LinkedList<String>();
        for(String player : leaderboardData.keySet()){
            sortedLeaderboard.add(leaderboardData.get(player).get(0)+" "+player);
        }
        Collections.sort(sortedLeaderboard,Collections.<String>reverseOrder());
        for(String data : sortedLeaderboard){
            String player = data.split(" ")[1];
            i++;
            if(i%2!=0){ //alternate colouring
                name = new Label(player,skin);
                kills = new Label(""+leaderboardData.get(player).get(0),skin);
                deaths = new Label(""+leaderboardData.get(player).get(1),skin);
                damage = new Label(""+leaderboardData.get(player).get(2),skin);
            }else {
                name = new Label(player, skin);
                kills = new Label("" + leaderboardData.get(player).get(0), skin);
                deaths = new Label("" + leaderboardData.get(player).get(1), skin);
                damage = new Label("" + leaderboardData.get(player).get(2), skin);
            }
            name.setWrap(true);
            kills.setWrap(true);
            deaths.setWrap(true);
            damage.setWrap(true);

            table.add(name).width(100f);
            table.add(kills).width(100f);
            table.add(deaths).width(100f);
            table.add(damage).width(100f);
            table.row();
        }
    }
    private void updateTeam(HashMap<String, LinkedList<Integer>> leaderboardData){
        redTable.clearChildren();
        blueTable.clearChildren();
        //0 = red
        //1 = blue
        int i = 0;
        LinkedList<String> sortedRed = new LinkedList<String>();
        LinkedList<String> sortedBlue = new LinkedList<String>();
        int emptySlots=6;
        for(String player : leaderboardData.keySet()){
            if(leaderboardData.get(player).get(3)==0){ //red player
                sortedRed.add(leaderboardData.get(player).get(0)+" "+player);
            }else{  //blue player
                sortedBlue.add(leaderboardData.get(player).get(0)+" "+player);
            }
        }
        emptySlots = Math.max(6-sortedRed.size(),6-sortedBlue.size());
        Collections.sort(sortedRed,Collections.<String>reverseOrder());
        Collections.sort(sortedBlue,Collections.<String>reverseOrder());
        for(String data : sortedRed){
            String player = data.split(" ")[1];
            i++;
            if(i%2!=0){ //alternate colouring
                name = new Label(player,skin);
                name.setWrap(true);
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
            kills.setWrap(true);
            deaths.setWrap(true);
            damage.setWrap(true);

            redTable.add(name).width(100f).padBottom(5f).padTop(5f);
            redTable.add(kills).width(50f);
            redTable.add(deaths).width(50f);
            redTable.add(damage).width(50f);
            redTable.row();
        }
        for(String data : sortedBlue){
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

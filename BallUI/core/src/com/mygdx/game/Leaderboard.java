package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import java.util.TreeMap;

public class Leaderboard {
    //Leaderboard keeps track of 4 things:
    //Kills, Deaths, Damage Dealt, and the associated Username
    static Table table;
    static TextureAtlas atlas;
    static Skin skin;
    static{
        atlas = new TextureAtlas("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.atlas");
        skin = new Skin(Gdx.files.internal("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.json"));
        skin.addRegions(atlas);
        table = new Table();
        table.setDebug(true);
        table.setFillParent(true);
        table.center();
    }
    public static void updateLeaderboard(TreeMap<String,Integer> killsByPlayer){
        for(String player : killsByPlayer.keySet()){
            Label name = new Label(player,skin);
            Label kills = new Label(""+killsByPlayer.get(player),skin);
            table.add(name).width(100f);
            table.add(kills).width(100f);
            table.row();
        }
        //table.clearChildren();

    }
}

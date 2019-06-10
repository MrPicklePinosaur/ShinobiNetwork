package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import java.util.LinkedList;
import java.util.TreeMap;
import java.util.Vector;

public class Launcher extends ApplicationAdapter {
    ChatLog cl;
    Texture minimap;
    private Stage stage;
    private Table table;
    private HealthTracker ht;
    TreeMap<String,Integer> players;
    @Override
    public void create () {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        table = new Table();
        table.setFillParent(true);
        table.setDebug(false);
        stage.addActor(table);
        cl = new ChatLog(table);
        minimap = new Texture("MiniMap.png");
        ht = new HealthTracker(new Vector2(100f,100f));

        String username = "Daniel Liu";
        String classname = "Scout";
        int level = 50;
        String query1 = "INSERT INTO players (username,class,level) VALUES ('u','c',1)";
        //String query1 = "INSERT INTO players (username,class,level) VALUES ('"+username+"', '"+classname+"', "+level+")";
        players = new TreeMap<String, Integer>();
        players.put("Shrey",1);
        players.put("Daniel",100);
    }

    @Override
    public void resize (int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void render () {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        ht.drawAll();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Timer.updateLives(20,20);
        Timer.updateTime(99.59f);
        cl.sr.begin(ShapeRenderer.ShapeType.Filled);
        //cl.sr.rect(1340,150,250,400,new Color(0,0,0,0.25f),new Color(0,0,0,0.25f),Color.BLACK,Color.BLACK);
        cl.sr.rect(1340,550,250,350,Color.CLEAR,Color.CLEAR,Color.BLUE,Color.BLUE);  //temporary space cover, will be replaced by minimap later
        cl.sr.end();
        Leaderboard.updateLeaderboard(players);
    }

    @Override
    public void dispose() {
        stage.dispose();
        ht.dispose();
    }}

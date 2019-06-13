package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

import java.util.HashMap;
import java.util.LinkedList;

public class HealthTracker {
    private static HashMap<Integer,HealthTracker> health_bars = new HashMap<Integer,HealthTracker>();

    private ProgressBar bar;
    private Label nameLabel;
    static Skin skin;
    public static final float y_offset = 25;
    static ProgressBar.ProgressBarStyle style;
    static Label.LabelStyle nameStyle;

    static{
        skin = new Skin(Gdx.files.internal("gdx-skins/clean-crispy/skin/clean-crispy-ui.json"));
        style = new ProgressBar.ProgressBarStyle();
        style.background = skin.getDrawable("progressbar-horizontal-c");
        style.knobBefore = skin.getDrawable("progressbar-knob-horizontal-c");
        style.background.setMinHeight(0f);
        style.knobBefore.setMinHeight(style.knobBefore.getMinHeight()*0.3f);
        nameStyle = new Label.LabelStyle();
        nameStyle.font = Global.skin.getFont("PixelFont");
        nameStyle.fontColor = Color.BLACK;
    }
    public HealthTracker(int id) {
        this.bar = new ProgressBar(0f,1f,0.005f,false,skin);
        this.bar.setStyle(style);
        this.bar.setWidth(this.getBarWidth()*0.5f);
        this.bar.setHeight(this.getBar().getMinHeight());
        this.bar.setValue(1f);
        //this.bar.setColor(Color.GREEN);
        health_bars.put(id,this);
        Global.game.game_screen.getStage().addActor(bar);
        //\progressBarStyle = skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        //tiledDrawable = skin.getTiledDrawable("progressbar-tiled").tint(skin.getColor("selection"));
        //tiledDrawable.setMinWidth(0);
        //progressBarStyle.knobBefore = tiledDrawable;

        this.nameLabel = new Label(Global.user_data.username,skin);
        this.nameLabel.setStyle(nameStyle);
        this.nameLabel.setFontScale(0.25f);
    }

    public static void drawAll(SpriteBatch batch){
        for(HealthTracker ht : HealthTracker.health_bars.values()){
            ht.getBar().act(Gdx.graphics.getDeltaTime());
            ht.getBar().draw(batch,1.0f);
            ht.nameLabel.setPosition(ht.getBar().getX()+ht.nameLabel.getPrefWidth()*ht.nameLabel.getFontScaleX(),ht.getBar().getY()+ht.getBar().getPrefHeight());
            ht.nameLabel.draw(batch,1f);
        }
    }

    public static void update_data(String[] data) {
        for (int i = 0; i < data.length; i ++) {
            String[] hp_data = data[i].split(",");

            int id = Integer.parseInt(hp_data[0]);
            float hp_percent = Float.parseFloat(hp_data[1]);

            if (!HealthTracker.health_bars.containsKey(id)) { continue; } //if the hp bar doesnt exist, forget about it for now

            HealthTracker.health_bars.get(id).setHealth(hp_percent);
        }
    }

   public void setHealth(float hp) {
        /*if(hp>=0.5f){
            this.bar.setColor(Color.GREEN);
        }
        if(hp<0.5f && hp>0.25f){
            this.bar.setColor(Color.YELLOW);
        }
        if(hp<=0.25f){
            this.bar.setColor(Color.RED);
        }*/
        this.bar.setValue(hp);
    }

    public void setPos(float x,float y) { this.bar.setPosition(x,y); }
    public float getBarWidth() { return this.bar.getWidth(); }

    public ProgressBar getBar() { return this.bar; }
    public static HashMap<Integer,HealthTracker> getHealthBarList() { return HealthTracker.health_bars; }
}

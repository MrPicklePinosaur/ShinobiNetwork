package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import java.util.HashMap;
import java.util.LinkedList;

public class HealthTracker {
    private static HashMap<Integer,HealthTracker> health_bars = new HashMap<Integer,HealthTracker>();

    private ProgressBar bar;

    public static final float y_offset = 32;

    public HealthTracker(int id) {
        this.bar = new ProgressBar(0f,1f,0.001f,false,Global.skin);
        health_bars.put(id,this);
        //\progressBarStyle = skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        //tiledDrawable = skin.getTiledDrawable("progressbar-tiled").tint(skin.getColor("selection"));
        //tiledDrawable.setMinWidth(0);
        //progressBarStyle.knobBefore = tiledDrawable;
    }

    public static void drawAll(SpriteBatch batch){
        for(HealthTracker ht : HealthTracker.health_bars.values()){
            ht.getBar().act(Gdx.graphics.getDeltaTime());
            ht.getBar().draw(batch,1.0f);
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
        this.bar.setValue(hp);
    }

    public void setPos(float x,float y) { this.bar.setPosition(x,y); }
    public float getBarWidth() { return this.bar.getMaxWidth(); }

    public ProgressBar getBar() { return this.bar; }
    public static HashMap<Integer,HealthTracker> getHealthBarList() { return HealthTracker.health_bars; }
}

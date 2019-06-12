package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.*;

import java.util.HashMap;
import java.util.LinkedList;

public class HealthTracker {
    private static HashMap<Integer,HealthTracker> health_bars = new HashMap<Integer,HealthTracker>();
    private ProgressBar bar;

    public HealthTracker(int id) {
        this.bar = new ProgressBar(0f,1f,0.01f,false,Global.skin);
        health_bars.put(id,this);
        //progressBarStyle = skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        //tiledDrawable = skin.getTiledDrawable("progressbar-tiled").tint(skin.getColor("selection"));
        //tiledDrawable.setMinWidth(0);
        //progressBarStyle.knobBefore = tiledDrawable;
    }

    public static void drawAll(SpriteBatch batch){
        for(HealthTracker ht : HealthTracker.health_bars.values()){
            ht.getBar().draw(batch,1.0f);
        }
    }

    public static void update_data(String[] data) {
        for (int i = 0; i < data.length; i ++) {
            String[] hp_data = data[i].split(",");

            int id = Integer.parseInt(hp_data[0]);
            float hp_percent = Float.parseFloat(hp_data[1]);

            HealthTracker ht = null;
            if (HealthTracker.health_bars.containsKey(id)) { ht = HealthTracker.health_bars.get(id); }
            else { ht = new HealthTracker(id); }

            assert (ht != null): "Failed to find hp bar";

            ht.setHealth(hp_percent);
        }
    }

    public static void sync_pos() {
        for (Integer id : HealthTracker.health_bars.keySet()) {
            if (Entity.getEntityLib().containsKey(id)) { //iif the entity is actually alive
                Entity e = Entity.getEntity(id);
                HealthTracker ht = HealthTracker.health_bars.get(id);

                ht.setPos(e.getX(),e.getY());
            }
        }
    }

   public void setHealth(float hp) {
        this.bar.setValue(hp);
    }
    public void setPos(float x,float y) { this.bar.setPosition(x,y); }

    public ProgressBar getBar() { return this.bar; }
    public static HashMap<Integer,HealthTracker> getHealthBarList() { return HealthTracker.health_bars; }
}

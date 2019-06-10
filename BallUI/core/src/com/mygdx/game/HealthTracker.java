package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;

import java.util.LinkedList;

public class HealthTracker{
    static Skin skin;
    static TextureAtlas atlas;
    static SpriteBatch batch;
    //static ProgressBar.ProgressBarStyle progressBarStyle;
    //static TiledDrawable tiledDrawable;
    static LinkedList<HealthTracker> HealthBars = new LinkedList<HealthTracker>();
    private float health;
    private Vector2 pos;
    private ProgressBar bar;
    static{
        skin = new Skin(Gdx.files.internal("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.json"));
        atlas = new TextureAtlas("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.atlas");
        skin.addRegions(atlas);
        batch = new SpriteBatch();
    }
    public HealthTracker(Vector2 v) {
        this.health = 1f;
        this.pos = v;
        this.bar = new ProgressBar(0f,1f,0.01f,false,skin);
        HealthBars.add(this);
        //progressBarStyle = skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        //tiledDrawable = skin.getTiledDrawable("progressbar-tiled").tint(skin.getColor("selection"));
        //tiledDrawable.setMinWidth(0);
        //progressBarStyle.knobBefore = tiledDrawable;
    }
    public void draw(float currHealth, Vector2 v){
        this.bar.setValue(currHealth);
        this.bar.setPosition(v.x,v.y);
        batch.begin();
        this.bar.draw(batch,1.0f);
        batch.end();
    }
    public static void drawAll(){
        for(HealthTracker ht : HealthBars){
            ht.draw(ht.getHealth(),ht.getPos());
        }
    }
    public void setHealth(float hp){this.health = hp;}
    public float getHealth(){return this.health;}
    public Vector2 getPos(){return this.pos;}
    public void setPos(float x,float y){this.pos.x = x; this.pos.y = y;}

    public void dispose(){skin.dispose();atlas.dispose();}
}

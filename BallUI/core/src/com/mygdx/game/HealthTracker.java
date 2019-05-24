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
    public HealthTracker() {
        batch = new SpriteBatch();
        atlas = new TextureAtlas("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.atlas");
        skin = new Skin(Gdx.files.internal("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.json"));
        skin.addRegions(atlas);
        //progressBarStyle = skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        //tiledDrawable = skin.getTiledDrawable("progressbar-tiled").tint(skin.getColor("selection"));
        //tiledDrawable.setMinWidth(0);
        //progressBarStyle.knobBefore = tiledDrawable;
    }
    public void updateHealthBar(int maxHealth, int currHealth, Vector2 v){
        ProgressBar bar = new ProgressBar(0f,(float)maxHealth,0.01f,false,skin);
        bar.setValue((float)currHealth);
        bar.setX(v.x);
        bar.setY(v.y);
        batch.begin();
        bar.draw(batch,1.0f);
        batch.end();
    }
}

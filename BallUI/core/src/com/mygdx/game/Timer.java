package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.TiledDrawable;
import com.badlogic.gdx.utils.Align;

public class Timer {
    static ShapeRenderer sr = new ShapeRenderer();
    static float height = Gdx.graphics.getHeight()/12f;
    static SpriteBatch batch = new SpriteBatch();
    static Skin skin = new Skin(Gdx.files.internal("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.json"));
    static TextureAtlas atlas = new TextureAtlas("gdx-skins-master/clean-crispy/skin/clean-crispy-ui.atlas");
    static Label timeLabel = new Label("0",skin);
    //static ProgressBar.ProgressBarStyle progressBarStyle;
    //static TiledDrawable tiledDrawable;
    //static ProgressBar redLivesBar = new ProgressBar(0f,20f,1f,false,skin);
    //static ProgressBar blueLivesBar = new ProgressBar(0f,20f,1f,false,skin);
    static{
        skin.addRegions(atlas);

        timeLabel.setColor(Color.GREEN);
        Label.LabelStyle timeLabelStyle = timeLabel.getStyle();
        timeLabelStyle.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        timeLabelStyle.font.getData().setScale(height/25f);//5f);
        timeLabel.setAlignment(Align.center);
        timeLabel.setPosition(Gdx.graphics.getWidth()/2f-timeLabel.getWidth()/2f,Gdx.graphics.getHeight()-height*3f/5f);

        //progressBarStyle = skin.get("default-horizontal", ProgressBar.ProgressBarStyle.class);
        //tiledDrawable = skin.getTiledDrawable("progressbar-tiled").tint(skin.getColor("selection"));
        //tiledDrawable = skin.getTiledDrawable("progressbar-knob-horizontal-c").tint(skin.getColor("selection"));
        //tiledDrawable.setMinWidth(0);
        //tiledDrawable.setMinHeight(100f);
        //progressBarStyle.knobBefore = tiledDrawable;
        //progressBarStyle.knobBefore.setMinWidth(progressBarStyle.knobBefore.getMinWidth()*4f);
        //progressBarStyle.knobBefore.setMinHeight(progressBarStyle.knobBefore.getMinHeight()*4f);
        //progressBarStyle.background.setMinWidth(progressBarStyle.background.getMinWidth()*4f);
        //progressBarStyle.background.setMinHeight(progressBarStyle.background.getMinHeight()*4f);

        //redLivesBar.setPosition(Gdx.graphics.getWidth()/2f,Gdx.graphics.getHeight()-height);
        //blueLivesBar.setPosition(1f,1f);

        //redLivesBar.setValue(20f);
        //blueLivesBar.setValue(20f);
    }
    public static void updateTime(float t){
        timeLabel.setText(""+t);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.rect(Gdx.graphics.getWidth()*2f/5f,Gdx.graphics.getHeight()-height,Gdx.graphics.getWidth()*1f/5f,height,Color.RED,Color.RED,Color.RED,Color.RED);
        sr.triangle(Gdx.graphics.getWidth()*3f/10f,Gdx.graphics.getHeight(),Gdx.graphics.getWidth()*2f/5f,Gdx.graphics.getHeight(),Gdx.graphics.getWidth()*2f/5f,Gdx.graphics.getHeight()-height,Color.CLEAR,Color.RED,Color.RED);
        sr.triangle(Gdx.graphics.getWidth()*7f/10f,Gdx.graphics.getHeight(),Gdx.graphics.getWidth()*6f/10f,Gdx.graphics.getHeight(),Gdx.graphics.getWidth()*6f/10f,Gdx.graphics.getHeight()-height,Color.CLEAR,Color.RED,Color.RED);
        sr.end();
        batch.begin();
        timeLabel.draw(batch,1f);
        //redLivesBar.draw(batch,1f);
        //blueLivesBar.draw(batch,1f);
        batch.end();
    }
    public static void updateLives(int redlives,int bluelives){
        //redLivesBar.draw(batch,1f);
        sr.begin(ShapeRenderer.ShapeType.Filled);
        /*
        x - z = a - b
        x - a = z - b
        a = x*1.004

        */
        //lefthand side
        sr.rect(Gdx.graphics.getWidth()/2.631578946f,Gdx.graphics.getHeight()/1.0650879f,-Gdx.graphics.getWidth()*2f/10f,height/1.666666667f,Color.NAVY,Color.NAVY,Color.NAVY,Color.NAVY);
        sr.rect(Gdx.graphics.getWidth()/2.65339967f,Gdx.graphics.getHeight()/1.05882353f, -Gdx.graphics.getWidth()/5.16129f*bluelives/20f,height/2.14285714f,Color.BLUE,Color.BLUE,Color.BLUE,Color.BLUE);
        //righthand side
        sr.rect(Gdx.graphics.getWidth()*6.2f/10f,Gdx.graphics.getHeight()/1.0650879f,Gdx.graphics.getWidth()*2f/10f,height/1.666666667f,Color.FOREST,Color.FOREST,Color.FOREST,Color.FOREST);
        sr.rect(Gdx.graphics.getWidth()/1.60481f,Gdx.graphics.getHeight()/1.05882353f, Gdx.graphics.getWidth()/5.16129f*redlives/20f,height/2.14285714f,Color.GREEN,Color.GREEN,Color.GREEN,Color.GREEN);
        for(int i=0;i<redlives;i++){
            sr.rect(Gdx.graphics.getWidth() / 1.60481f + i * 16f, Gdx.graphics.getHeight() / 1.05882353f, Gdx.graphics.getWidth() * 0.00375f, height / 2.14285714f, Color.FOREST, Color.FOREST, Color.FOREST, Color.FOREST);
            //15-->15.26, 0.00625-->0.003
        }
        for(int i=0;i<bluelives;i++){
            sr.rect(Gdx.graphics.getWidth() / 2.65339967f - i * 16f, Gdx.graphics.getHeight() / 1.05882353f, -Gdx.graphics.getWidth() * 0.00375f, height / 2.14285714f, Color.NAVY,Color.NAVY,Color.NAVY,Color.NAVY);
        }
        //687 = 1600*1.60481 + 20*x +
        //310 = 20*x + 1600*0.0015f
        //310 = 19*x + 1600*0.0015f
        sr.end();
    }
}

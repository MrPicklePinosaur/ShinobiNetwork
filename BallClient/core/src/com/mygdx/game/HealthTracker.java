/* Shinobi Network
 __  __   ______   ______   __       ______  __  __       ______  ______   ______   ______   __  __   ______   ______
/\ \_\ \ /\  ___\ /\  __ \ /\ \     /\__  _\/\ \_\ \     /\__  _\/\  == \ /\  __ \ /\  ___\ /\ \/ /  /\  ___\ /\  == \
\ \  __ \\ \  __\ \ \  __ \\ \ \____\/_/\ \/\ \  __ \    \/_/\ \/\ \  __< \ \  __ \\ \ \____\ \  _"-.\ \  __\ \ \  __<
 \ \_\ \_\\ \_____\\ \_\ \_\\ \_____\  \ \_\ \ \_\ \_\      \ \_\ \ \_\ \_\\ \_\ \_\\ \_____\\ \_\ \_\\ \_____\\ \_\ \_\
  \/_/\/_/ \/_____/ \/_/\/_/ \/_____/   \/_/  \/_/\/_/       \/_/  \/_/ /_/ \/_/\/_/ \/_____/ \/_/\/_/ \/_____/ \/_/ /_/

HealthTracker class:
This class is responsible for creating "HealthTracker" objects
that are tracked in a master list of HealthTrackers, where
each HealthTracker consists of a health bar and username tag
above its respective player's name (each HealthTracker is
instantiated per player).
This class keeps track of the player's health, and updates it
on the health bar accordingly.
The 'health bar' is actually a Scene2d ProgressBar, with the max
value being the maximum possible health (full health - 1) and the
min value being the minimum possible health (no health - 0), with
the value of the ProgressBar knob being the frequency of the
current health over the player's maximum health (this may vary
depending on the player's class)

Methods (that aren't setters/getters):
drawAll:
    simply changes all HealthTrackers ProgressBars values to their respective players' health,
    draws each HealthTracker ProgressBar relative to their respective players' position,
    and draws each players' username tag at their respective positions
update_data:
    method that takes in data of all players of the players HealthTracker id with the player's health.
    The HealthTracker associated with the players' id has its ProgressBar's value set to the player's health
*/

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
    public static final float y_offset = 25;    //how much the healthbar is located above the player's body by

    static Skin skin;   //the Scene2d skin associated with each and every healthbar
    static ProgressBar.ProgressBarStyle style;  //the style (background image, knob image of progressbar) of progressbar
    static Label.LabelStyle nameStyle;  //the style of the username tag that is displayed above the player

    private ProgressBar bar;    //the player's healthbar
    private Label nameLabel;    //the player's username tag
    private int id; //the player's id associated with each HealthTracker

    static{
        skin = new Skin(Gdx.files.internal("gdx-skins/clean-crispy/skin/clean-crispy-ui.json"));    //getting the skin
        style = new ProgressBar.ProgressBarStyle();
        style.background = skin.getDrawable("progressbar-horizontal-c");    //set progressbar's background image from skin json
        style.knobBefore = skin.getDrawable("progressbar-knob-horizontal-c");   //set progressbar's knob image from skin json
        style.background.setMinHeight(0f);  //set minimum height of the progressbar style in order to let us make the progressbar shorter than normally allowed
        style.knobBefore.setMinHeight(style.knobBefore.getMinHeight()*0.3f);    //this is the minimum height that we actually set the healthbar's height to
        nameStyle = new Label.LabelStyle();
        nameStyle.font = Global.skin.getFont("PixelFont");  //set username tag's font from skin json
        nameStyle.fontColor = Color.BLACK;  //set username tag's font color to black
    }
    public HealthTracker(int id) {  //constructor that sets up the HealthTracker to keep track of a specific players' health
        this.id = id;   //associate the player's id with the newly created HealthTracker
        this.bar = new ProgressBar(0f,1f,0.005f,false,skin);    //our healthbar
        this.bar.setStyle(style);
        this.bar.setWidth(this.getBarWidth()*0.5f);
        this.bar.setHeight(this.getBar().getMinHeight());
        this.bar.setValue(1f);

        health_bars.put(id,this);   //add new HealthTracker to list of HealthTrackers for all players
        Global.game.game_screen.getStage().addActor(bar);

        this.nameLabel = new Label(Global.user_data.username,skin);
        this.nameLabel.setStyle(nameStyle);
        this.nameLabel.setFontScale(0.25f);
    }

    public static void drawAll(SpriteBatch batch){
        //go through all HealthTrackers,
        //draw each healthbar (while updating it if necessary),
        //and position + draw each username tag above each player
        for(HealthTracker ht : HealthTracker.health_bars.values()) {

            ht.getBar().act(Gdx.graphics.getDeltaTime());
            ht.getBar().draw(batch,1.0f);
            ht.nameLabel.setPosition(ht.getBar().getX()+ht.nameLabel.getPrefWidth()*ht.nameLabel.getFontScaleX(),ht.getBar().getY()+ht.getBar().getPrefHeight());
            ht.nameLabel.draw(batch,1f);
        }
    }

    public static void update_data(String[] data) {
        //for all players, get the id that is associated
        //with their HealthTrackers, and use it to make
        //each player's unique HealthTracker update with
        //the player's current health
        for (int i = 0; i < data.length; i ++) {
            String[] hp_data = data[i].split(",");

            int id = Integer.parseInt(hp_data[0]);
            float hp_percent = Float.parseFloat(hp_data[1]);

            if (!HealthTracker.health_bars.containsKey(id)) { continue; } //if the hp bar doesnt exist, forget about it for now


            HealthTracker.health_bars.get(id).setHealth(hp_percent);
        }
    }

    public void setHealth(float hp) { this.bar.setValue(hp); }
    public void setPos(float x,float y) { this.bar.setPosition(x,y); }
    public static void removeBar(int id) {
        assert (HealthTracker.health_bars.containsKey(id)): "Health bar cannot be removed as it was not found";
        HealthTracker.health_bars.get(id).getBar().remove();
        HealthTracker.health_bars.remove(id);
    }
    public static void clearBars() { HealthTracker.health_bars.clear(); }

    public float getBarWidth() { return this.bar.getWidth(); }
    public ProgressBar getBar() { return this.bar; }
    public int getId() { return this.id; }
    public static HashMap<Integer,HealthTracker> getHealthBarList() { return HealthTracker.health_bars; }
}

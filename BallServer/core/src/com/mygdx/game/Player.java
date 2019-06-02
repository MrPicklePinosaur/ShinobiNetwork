/*
 ______  __       ______   __  __   ______   ______
/\  == \/\ \     /\  __ \ /\ \_\ \ /\  ___\ /\  == \
\ \  _-/\ \ \____\ \  __ \\ \____ \\ \  __\ \ \  __<
 \ \_\   \ \_____\\ \_\ \_\\/\_____\\ \_____\\ \_\ \_\
  \/_/    \/_____/ \/_/\/_/ \/_____/ \/_____/ \/_/ /_/

 */

package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import javafx.util.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Player extends Entity {
    private HashMap<String,ActiveEffect> activeEffects_list = new HashMap<String, ActiveEffect>();

    public PlayerStats stats;
    private Ability ability;
    private float m_angle;
    private TEAMTAG teamtag;
    private Weapon weapon;

    //stats
    private int health;
    private int kills;
    private int deaths;
    private int dmg_dealt;

    public Player(String name,String json_stat_data,TEAMTAG teamtag) {
        super(name);
        this.entity_type = ET.PLAYER;
        this.teamtag = teamtag;
        this.m_angle = 0;

        //init player body
        CircleShape circle = new CircleShape(); //Players have a circular fixture
        circle.setRadius((this.spriteWidth/4f)/Global.PPM); //diameter of the circle is half of the width of the entity
        FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;
        fdef.filter.categoryBits = Global.BIT_PLAYER;
        fdef.filter.maskBits = Global.BIT_STATIC | Global.BIT_PLAYER | Global.BIT_PROJECTILE;
        this.body = AssetManager.createBody(fdef,BodyDef.BodyType.DynamicBody);
        this.body.setUserData(new Pair<Class<?>,Player>(Player.class,this));
        //this.body.setUserData(this);
        this.body.setLinearDamping(Global.PLAYER_DAMPING);

        circle.dispose();

        this.init_stats(json_stat_data);

        String weapon_name = "kazemonji";
        this.weapon = new Weapon(weapon_name,AssetManager.getWeaponJsonData(weapon_name),this);

    }

    public void handleInput(String raw_inputs) { //takes in user inputs from client and does physics simulations
        String[] inputs = raw_inputs.split(",");
        this.body.setLinearVelocity(0,0); //reset velocity
        for (String key : inputs) {
            if (key.contains("MOUSE_ANGLE:")) {
                String[] data = key.split(":");
                this.m_angle = Float.parseFloat(data[1]);
            }
            //if (key.equals("Key_Q")) { this.newProjectile("katanaSlash.png",this.m_angle); }
            if (key.equals("MOUSE_LEFT")) {
                String projectile_name = this.weapon.stats.getProjectileName();
                this.shoot(projectile_name,this.m_angle,this.weapon.stats.getFirePattern());
                this.newProjectile(projectile_name,this.m_angle);
            } //shoot bullet
            if (key.equals("Key_W")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,this.stats.getSpeed()); }
            if (key.equals("Key_S")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,-this.stats.getSpeed()); }
            if (key.equals("Key_A")) { this.body.setLinearVelocity(-this.stats.getSpeed(),this.body.getLinearVelocity().y); }
            if (key.equals("Key_D")) { this.body.setLinearVelocity(this.stats.getSpeed(),this.body.getLinearVelocity().y); }
            if (key.equals("Key_SPACE")) { this.ability.begin(); }
        }
    }

    //public float getMouseAngle() { return this.m_angle; }
    @Override public float getRotation() { return this.m_angle; }
    public TEAMTAG getTeamtag() { return this.teamtag; }
    public Weapon getWeapon() { return this.weapon; }
    public float getMouseAngle() { return this.m_angle; }
    public HashMap<String, ActiveEffect> getActiveEffectsList() { return this.activeEffects_list; }
    public void removeEffect(String effect) {
        assert (this.activeEffects_list.containsKey(effect)): "Unable to remove effect";
        this.activeEffects_list.remove(effect);
    }

    //STATS STUFF
    public int getCurrentHp() { return this.health; }
    public Boolean modHp(int deltaHp) { //returns wether or not the player was killed
        this.health += deltaHp;
        this.health = MathUtils.clamp(this.health,0,this.stats.getHp()); //clamped so hp doesnt exceed max hp
        if (this.health <= 0) {
            Vector2 spawn_point = Global.map.get_spawn_point(this.getTeamtag());
            AssetManager.flagForMove(this,new Vector3(spawn_point.x,spawn_point.y,this.getRotation()));
            this.reset_game_stats();
            return true;
        }
        return false;
    }

    public void applyActiveEffect(String effect_name,float duration) {
        if (!this.activeEffects_list.containsKey(effect_name)) { //if the effect isnt already active, add it
            this.activeEffects_list.put(effect_name,new ActiveEffect(this,effect_name,duration));
        }
        this.activeEffects_list.get(effect_name).resetDurationTimer(); //if the effect is already active, simply reset the timer
    }

    @Override public void init_stats(String json_data) { //should be called once, or when player respawns
        this.stats = Global.json.fromJson(PlayerStats.class,json_data);

        //insert code that modifies base stats based on items equiped
        this.reset_game_stats();
        this.ability = Ability.createAbility(this,this.stats.getAblType(),"whirlwind");
    }

    public void reset_game_stats() { this.health = this.stats.getHp(); }
    /*
    public void reset_performance_stats() { //used when the game resets
        this.kills = 0;
        this.deaths = 0;
        this.dmg_dealt = 0;
    }
    */

    //stat setters
    public void addKill() { this.kills++; }
    public void addDeath() { this.deaths++; }
    public void addDmgDealt(int dmg_dealt) { this.dmg_dealt+= dmg_dealt; }

    //stat getters
    //possibly remove all the individual getters
    public int getKills() { return this.kills; }
    public int getDeaths() { return this.deaths; }
    public int getDmgDealt() { return this.dmg_dealt; }
    public Vector3 getGameStats() { return new Vector3(this.kills,this.deaths,this.dmg_dealt); }
}

class PlayerStats {
    private String name;
    private int hp;
    private int speed;
    private String abl_type;

    public PlayerStats() { } //not sure why you need a no arg constructor, but you need one

    //Getters
    public String getName() { return this.name; }
    public int getHp() { return this.hp; }
    public int getSpeed() { return this.speed; }
    public String getAblType() { return this.abl_type; }

    public void setSpeed(int speed) { this.speed = speed; }
}


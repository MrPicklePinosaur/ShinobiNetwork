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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Player extends Entity {
    private static CopyOnWriteArrayList<Player> shoot_cooldown_list = new CopyOnWriteArrayList<Player>();
    private static CopyOnWriteArrayList<Player> hold_list = new CopyOnWriteArrayList<Player>();
    private ConcurrentHashMap<String,ActiveEffect> activeEffects_list = new ConcurrentHashMap<String, ActiveEffect>();

    private BallClientHandler server_socket;
    public PlayerStats stats;
    private Ability ability;
    private float m_angle;
    private TEAMTAG teamtag;

    private Weapon weapon;

    private float shoot_cooldown;
    private float hold_count;
    private float dmg_mult;

    //stats
    private float health;
    private float speed;

    //performance stats
    private int kills;
    private int deaths;
    private float dmg_dealt;

    public Player(String name,String weapon_name,String ability_name,TEAMTAG teamtag,BallClientHandler server_socket) {
        super(name);
        this.server_socket = server_socket;
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
        if (this.teamtag == TEAMTAG.RED) {
            fdef.filter.maskBits = Global.BIT_STATIC | Global.BIT_PLAYER | Global.BIT_PROJECTILE | Global.BIT_BLUESTATIC;
        } else if (this.teamtag == TEAMTAG.BLUE) {
            fdef.filter.maskBits = Global.BIT_STATIC | Global.BIT_PLAYER | Global.BIT_PROJECTILE | Global.BIT_REDSTATIC;
        }

        this.body = AssetManager.createBody(fdef,BodyDef.BodyType.DynamicBody);
        this.body.setUserData(new Pair<Class<?>,Player>(Player.class,this));
        this.body.setLinearDamping(Global.PLAYER_DAMPING);
        circle.dispose();

        init_data(name,weapon_name,ability_name);

    }

    @Override public void init_stats(String json_data) { //should be called once, or when player respawns
        this.stats = Global.json.fromJson(PlayerStats.class,json_data);

        //insert code that modifies base stats based on items equiped
        this.reset_game_stats();
        this.reset_performance_stats();
    }

    public void init_data(String class_name,String weapon_name,String ability_name) {
        this.name = class_name;
        this.weapon = new Weapon(weapon_name,this);
        this.init_stats(AssetManager.getPlayerJsonData(class_name));
        this.ability = Ability.createAbility(this,this.stats.getAblType(),ability_name);
    }


    public void reset_game_stats() {
        this.health = this.stats.getHp();
        this.speed = this.stats.getSpeed();
        this.resetShootCoolDown();
        this.resetHoldCount();
        this.resetDmgMult();
    }

    public void reset_performance_stats() { //used when the game resets
        this.kills = 0;
        this.deaths = 0;
        this.dmg_dealt = 0;
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
            if (key.equals("MOUSE_LEFT_DOWN")) {

                if (this.weapon.stats.getFireType().equals("charged")) { //keep track of how long the mouse is held if its a charged weapon
                    this.resetHoldCount();
                    Player.hold_list.add(this);
                }

            } //shoot bullet
            if (key.equals("MOUSE_LEFT_UP")) {
                if (Global.game.isInsideSpawn(this)) { continue; } //player cant shoot when inside spawn

                if (this.weapon.stats.getFireType().equals("charged")) { Player.hold_list.remove(this); }

                //used for charged weapons
                float time_held = this.hold_count;

                float charge_dmg_mult = this.findDmgMult(this.weapon.stats.getTimeToCharge(),time_held);
                float charge_speed_mult = this.findSpeedMult(this.weapon.stats.getTimeToCharge(),time_held);


                //shoot projectile only when mouse is released
                if (Player.shoot_cooldown_list.contains(this)) { break; } //if the player is currently under shoot cooldwon ,dont shoot

                //multiply the damage multiplier the player already has and the multiplier gained from charging
                this.shoot(this.weapon.stats.getProjectileName(), this.m_angle, this.weapon.stats.getFirePattern(), this.dmg_mult*charge_dmg_mult,charge_speed_mult);

                this.resetShootCoolDown();
                Player.shoot_cooldown_list.add(this);
            }
            if (key.equals("Key_W")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,this.stats.getSpeed()); }
            if (key.equals("Key_S")) { this.body.setLinearVelocity(this.body.getLinearVelocity().x,-this.stats.getSpeed()); }
            if (key.equals("Key_A")) { this.body.setLinearVelocity(-this.stats.getSpeed(),this.body.getLinearVelocity().y); }
            if (key.equals("Key_D")) { this.body.setLinearVelocity(this.stats.getSpeed(),this.body.getLinearVelocity().y); }
            if (key.equals("Key_SPACE")) {
                if (Global.game.isInsideSpawn(this)) { continue; } //player cant use ability when inside spawn
                this.ability.begin();
            }
        }
    }

    public float findDmgMult(float time_to_charge,float time_held) {
        if (time_held < time_to_charge*1/3) { return 1; } //no charge
        if (time_held < time_to_charge*2/3) { return 1.2f; } //medium charge
        else if (time_held >= time_to_charge) { return 1.5f; } //max_charge
        return 1;
    }
    public float findSpeedMult(float time_to_charge,float time_held) {
        if (time_held < time_to_charge*1/3) { return 1; } //no charge
        else if (time_held < time_to_charge*2/3) { return 1.5f; } //medium charge
        else if (time_held >= time_to_charge) { return 2f; }//max_charge
        return 1;
    }

    public void applyActiveEffect(String effect_name,float duration) {
        //if the effect is already active and the duration left is less than the new duration, reset the time
        if (this.activeEffects_list.containsKey(effect_name) && this.activeEffects_list.get(effect_name).getDurationLeft() < duration) {
            this.activeEffects_list.get(effect_name).resetDurationTimer(); //if the effect is already active, simply reset the timer
        } else if (!this.activeEffects_list.containsKey(effect_name)) { //if the effect isnt already active, add it
            ActiveEffect effect = new ActiveEffect(this,effect_name,duration);
            this.activeEffects_list.put(effect_name,effect);
            effect.begin();
        }

    }

    public static void updateAll(float deltaTime) {
        //SHOOT COOLDOWN
        ArrayList<Player> removal_list = new ArrayList<Player>();
        for (Player p : Player.shoot_cooldown_list) {
            p.shoot_cooldown-=deltaTime;
            if (p.shoot_cooldown <= 0) { removal_list.add(p); }
        }
        for (Player p : removal_list) {
            assert (Player.shoot_cooldown_list.contains(p)): "Player is not under cooldown and therefore cannot be removed";
            Player.shoot_cooldown_list.remove(p);
        }

        //TIME HELD FOR MOUSE
        for (Player p : Player.hold_list) { p.hold_count+=deltaTime; }

    }

    //getters
    @Override public float getRotation() { return this.m_angle; }
    public TEAMTAG getTeamtag() { return this.teamtag; }
    public Weapon getWeapon() { return this.weapon; }
    public float getMouseAngle() { return this.m_angle; }
    public ConcurrentHashMap<String, ActiveEffect> getActiveEffectsList() { return this.activeEffects_list; }
    public float getDmgMult() { return this.dmg_mult; }
    public float getCurrentHp() { return this.health; }
    public float getSpeed() { return this.speed; }

    //setters
    public void resetShootCoolDown() { this.shoot_cooldown = this.weapon.stats.getFireRate(); }
    public void resetHoldCount() { this.hold_count = 0; }
    public void resetDmgMult() { this.dmg_mult = 1; }
    public void removeEffect(String effect) {
        assert (this.activeEffects_list.containsKey(effect)): "Unable to remove effect";
        this.activeEffects_list.remove(effect);
    }
    public void setDmgMult(float dmg_mult) { this.dmg_mult = dmg_mult; }
    public void setSpeed(float speed) { this.speed = speed; }
    public Boolean modHp(float deltaHp) { //returns wether or not the player was killed

        if (Global.game.isInsideSpawn(this)) { return false; } //if player is in spawn point, dont take damage

        this.health += deltaHp;
        this.health = MathUtils.clamp(this.health,0,this.stats.getHp()); //clamped so hp doesnt exceed max hp

        //update clients with new hp values
        String hp = Global.game.getAllHP();
        if (!hp.equals("")) { BallClientHandler.broadcast(MT.UPDATEHP,hp); }

        if (this.health <= 0) {
            Vector2 spawn_point = Global.map.get_spawn_point(this.getTeamtag());
            AssetManager.flagForMove(this,new Vector3(spawn_point.x,spawn_point.y,this.getRotation()));

            //TODO: CLEAR ALL ACTIVE EFFECTS

            this.reset_game_stats();

            //ask user to choose new class
            server_socket.send_msg(MT.CHOOSECLASS,"");

            return true;
        }
        return false;
    }

    //stat setters
    public void addKill() { this.kills++; }
    public void addDeath() { this.deaths++; }
    public void addDmgDealt(float dmg_dealt) { this.dmg_dealt+= dmg_dealt; }

    //stat getters
    //possibly remove all the individual getters
    public int getKills() { return this.kills; }
    public int getDeaths() { return this.deaths; }
    public float getDmgDealt() { return this.dmg_dealt; }
    public Vector3 getGameStats() { return new Vector3(this.kills,this.deaths,this.dmg_dealt); }
}

class PlayerStats {
    private String name;
    private int hp;
    private float speed;
    private String abl_type;

    public PlayerStats() { }

    //Getters
    public String getName() { return this.name; }
    public int getHp() { return this.hp; }
    public float getSpeed() { return this.speed; }
    public String getAblType() { return this.abl_type; }
}


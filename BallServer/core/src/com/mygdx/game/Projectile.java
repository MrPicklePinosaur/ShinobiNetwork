/*
 ______  ______   ______      __   ______   ______   ______  __   __       ______
/\  == \/\  == \ /\  __ \    /\ \ /\  ___\ /\  ___\ /\__  _\/\ \ /\ \     /\  ___\
\ \  _-/\ \  __< \ \ \/\ \  _\_\ \\ \  __\ \ \ \____\/_/\ \/\ \ \\ \ \____\ \  __\
 \ \_\   \ \_\ \_\\ \_____\/\_____\\ \_____\\ \_____\  \ \_\ \ \_\\ \_____\\ \_____\
  \/_/    \/_/ /_/ \/_____/\/_____/ \/_____/ \/_____/   \/_/  \/_/ \/_____/ \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import javafx.util.Pair;

import java.util.ArrayList;

class Projectile extends Entity {

    public ProjectileStats stats;
    private Entity owner; //keeps track of who created the projectile

    private float spawnX;
    private float spawnY;

    private float damage;
    private float speed;
    private int totalPenetrations;

    public Projectile(String name,Entity owner) {
        super(name);
        this.stats = Global.json.fromJson(ProjectileStats.class,AssetManager.getProjectileJsonData(name));
        this.entity_type = ET.PROJECTILE;
        this.owner = owner;

        //create porjectile body
        CircleShape circle = new CircleShape();
        circle.setRadius((this.spriteWidth/4f)/Global.PPM); //diameter of the circle is half of the width of the projecile
        FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;

        fdef.filter.categoryBits = Global.BIT_PROJECTILE;
        fdef.filter.maskBits = Global.BIT_STATIC | Global.BIT_PLAYER | Global.BIT_REDSTATIC | Global.BIT_BLUESTATIC;

        fdef.isSensor = true;
        this.body = AssetManager.createBody(fdef, BodyDef.BodyType.DynamicBody);
        this.body.setUserData(new Pair<Class<?>,Projectile>(Projectile.class,this));

        circle.dispose();

        //init stats
        this.damage = this.stats.getDamage();
        this.speed = this.stats.getBulletSpeed();
        this.totalPenetrations = this.stats.getPenetraion();
    }

    @Override public void init_pos(float x, float y, float rotation) {
        super.init_pos(x,y,rotation);
        this.spawnX = x;
        this.spawnY = y;
    }
    @Override public void init_stats(String json_data) {
        this.stats = Global.json.fromJson(ProjectileStats.class, json_data);
    }

    public static void updateAll() { //check to see if porpjectile is supposed to die

        for (Player p : Global.game.getPlayerList()) {
            ArrayList<Projectile> remove_list = new ArrayList<Projectile>(); //list of projectiles to be destroyed

            for (Projectile b : p.getProjectileList()) {
                //if the max distance is execeeded
                if (Math.hypot(b.spawnX-b.getX()/Global.PPM,b.spawnY-b.getY()/Global.PPM)>=b.stats.getMaxDist()) { remove_list.add(b); }
            }
            for (Projectile b : remove_list) {
                b.removeProjecitle();
            }
        }
    }

    public void checkPenetration() { //called when bullet hits a target, checks to see if bullet should pass through or die
        this.totalPenetrations--;
        if (this.totalPenetrations <= 0) { //then the bullet should die
            this.removeProjecitle();
        }
    }

    public void hit_effect(Player target,float damage) { //if a bullet has a unique effect when it hits a target, put it here
        String name = this.stats.getName();
        Player owner = (Player) this.owner;

        //katanas
        if (name.equals("brimstone_slice")) { //7% chanceto inflict burning for 3 seconds
            int chance = Global.rnd.nextInt(100);
            if (chance < 7) { target.applyActiveEffect("burning",3); }

        } else if (name.equals("jade_slice")) { //upon hitting a target, the fire rate increaes

        }

        //waki
        else if (name.equals("tsuinejji_fire_slash")) { //has a chance to inflict burning
            int chance = Global.rnd.nextInt(100);
            if (chance < 50) { target.applyActiveEffect("burning",2); }

        } else if (name.equals("tsuinejji_ice_slash")) {//has a chance to inflict frostbite
            int chance = Global.rnd.nextInt(100);
            if (chance < 50) { target.applyActiveEffect("frostbite", 2); }

        } else if (name.equals("bloodsucker_slash")) { //takes half of the damage dealt and heals self
            owner.modHp((int)(damage/2));

        }

        //spells
        else if (name.equals("flamethrower_bolt")) {
            int chance = Global.rnd.nextInt(100);
            if (chance < 40) { target.applyActiveEffect("burning",1); }
        }


    }

    public void kill_effect(Player target) { //if a bullet has a unique effect when it kills a target, put it here
        String name = this.stats.getName();
        Player owner = (Player) this.owner;

        if (name.equals("nihiru_slice")) {
            owner.setDmgMult(owner.getDmgMult()*1.1f);

        } else if (name.equals("jade_slice")) {
            owner.shoot("jade_slice",owner.getRotation(),"ring",owner.getDmgMult(),1);

        }
    }

    //setters
    public void setVelocity(float angle) { this.body.setLinearVelocity(this.getSpeed()* MathUtils.cos(angle),this.getSpeed()*MathUtils.sin(angle)); }
    public void setDamage(float dmg) { this.damage = dmg; }
    public void setSpeed(float speed) { this.speed = speed; }
    public void removeProjecitle() {
        this.owner.removeProjectile(this);
    }

    //getters
    public Entity getOwner() { return this.owner; }
    public float getDamage() { return this.damage; }
    public float getSpeed() { return this.speed; }

}

class ProjectileStats { //stats for projectile, loaded from json

    private String name;
    private float damage;
    private float bullet_speed;
    private float max_dist;
    private int penetration;
    private String travel_pattern;

    public ProjectileStats() { }

    public String getName() { return this.name; }
    public float getDamage() { return this.damage; }
    public float getBulletSpeed() { return this.bullet_speed; }
    public float getMaxDist() { return this.max_dist; }
    public int getPenetraion() { return this.penetration; }
    public String getTravelPattern() { return this.travel_pattern; }

}

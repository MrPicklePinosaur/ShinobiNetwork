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

    public Projectile(String name,String json_stat_data,Entity owner) {
        super(name);
        this.stats = Global.json.fromJson(ProjectileStats.class,json_stat_data);
        this.entity_type = ET.PROJECTILE;
        this.owner = owner;


        //init body
        /*
        PolygonShape rect = new PolygonShape();
        rect.setAsBox((float)this.spriteWidth/Global.PPM,(float)this.spriteHeight/Global.PPM);
        FixtureDef fdef = new FixtureDef();
        fdef.shape = rect;
        */
        CircleShape circle = new CircleShape();
        circle.setRadius((this.spriteWidth/4f)/Global.PPM); //diameter of the circle is half of the width of the projecile
        FixtureDef fdef = new FixtureDef();
        fdef.shape = circle;

        fdef.filter.categoryBits = Global.BIT_PROJECTILE;
        fdef.filter.maskBits = Global.BIT_STATIC | Global.BIT_PLAYER;
        fdef.isSensor = true;
        this.body = AssetManager.createBody(fdef, BodyDef.BodyType.DynamicBody);
        this.body.setUserData(new Pair<Class<?>,Projectile>(Projectile.class,this));
        //this.body.setUserData(this);

        circle.dispose();

        this.damage = this.stats.getDamage();
        this.speed = this.stats.getBulletSpeed();
        this.totalPenetrations = this.stats.getPenetraion();
    }

    @Override public void init_pos(float x, float y, float rotation) {
        super.init_pos(x,y,rotation);
        this.spawnX = x;
        this.spawnY = y;
    }

    public void setVelocity(float angle) {
        this.body.setLinearVelocity(this.stats.getBulletSpeed()* MathUtils.cos(angle),this.stats.getBulletSpeed()*MathUtils.sin(angle));
    }

    @Override public void init_stats(String json_data) {
        this.stats = Global.json.fromJson(ProjectileStats.class, json_data);
    }

    public static void updateAll() {

        for (Player p : Global.game.getPlayerList()) {
            ArrayList<Projectile> remove_list = new ArrayList<Projectile>();
            //p.getProjectileList().removeIf(b -> (Math.hypot(b.getX()-b.spawnX,b.getY()-b.spawnY)>=b.stats.getMaxDist()));
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

        if (name.equals("brimstone_slice")) { //7% chanceto inflict burning for 3 seconds
            int chance = Global.rnd.nextInt(100);
            if (chance < 7) {
                target.applyActiveEffect("burning",3);
            }
        } else if (name.equals("jade_slice")) { //upon hitting a target, the fire rate increaes

        }

    }

    public void kill_effect(Player target) { //if a bullet has a unique effect when it kills a target, put it here
        String name = this.stats.getName();
        Player owner = (Player) this.owner;

        if (name.equals("nihiru_slice")) {
            owner.setDmgMult(owner.getDmgMult()*1.1f);
        } else if (name.equals("jade_slice")) {
            owner.shoot("jade_slice",owner.getMouseAngle(),"ring",owner.getDmgMult(),1);
        }
    }


    public void removeProjecitle() {
        this.owner.removeProjectile(this);
    }
    public Entity getOwner() { return this.owner; }
    public float getDamage() { return this.damage; }
    public float getSpeed() { return this.speed; }

    public void setDamage(float dmg) { this.damage = dmg; }
    public void setSpeed(float speed) { this.speed = speed; }
}

class ProjectileStats {

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

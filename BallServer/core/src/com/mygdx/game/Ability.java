package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

import java.util.concurrent.CopyOnWriteArrayList;

public abstract class Ability {
    protected static CopyOnWriteArrayList<Ability> active_abilities = new CopyOnWriteArrayList<Ability>(); //list of abilities current being used
    protected static CopyOnWriteArrayList<Ability> cooldown_abilities = new CopyOnWriteArrayList<Ability>(); //list of abbilities currently under cooldown

    protected String name;
    protected float max_duration;
    protected float max_cooldown;
    protected Player player;

    //vars used to update the ability
    protected float duration_left; //more accurate way of recording how much time left in ability
    protected float cooldown_left;
    protected int ticker; //counts how many ticks (less accurate, but easy to divide)

    public Ability() { }

    public static Ability createAbility(Player player, String abl_type,String abl_name) {
        Ability newAbility = null;
        if (abl_type.equals("swiftstrike")) {
            //System.out.println(AssetManager.getAbilityJsonData(abl_type,abl_name));
            newAbility = Global.json.fromJson(SwiftstrikeAbility.class,AssetManager.getAbilityJsonData(abl_type,abl_name));
        } else if (abl_type.equals("warcry")) {
            newAbility = Global.json.fromJson(WarcryAbility.class,AssetManager.getAbilityJsonData(abl_type,abl_name));
        } else if (abl_type.equals("quiver")) {
            newAbility = Global.json.fromJson(QuiverAbility.class, AssetManager.getAbilityJsonData(abl_type, abl_name));
        } else if (abl_type.equals("spell")) {
            newAbility = Global.json.fromJson(SpellAbility.class, AssetManager.getAbilityJsonData(abl_type, abl_name));
        }
        assert (newAbility != null): "Failed to create ability";
        newAbility.bind_player(player);
        return newAbility;
    }

    public void bind_player(Player player) { this.player = player; } //attach a player to the ability

    public abstract void activate(); //what to do when ability is activated
    public abstract void update(); //what to do when ticking ability
    public abstract void deactivate(); //what to do when ability is deactivated

    public void begin() {
        assert (this.player != null): "Player hasn't been binded yet!!";

        //you cant activate ability if its already activated or under cool down
        if (Ability.cooldown_abilities.contains(this) || Ability.active_abilities.contains(this)) { return; }

        Ability.active_abilities.add(this);
        this.activate(); //trigger acrivation phase
    }

    public static void updateAll(float deltaTime) { //tick abilities
        for (Ability a : Ability.active_abilities) {
            a.update(); //trgger update phase
            a.tickAbilityDuration(deltaTime);
            a.tickTicker();
        }
        for (Ability c : Ability.cooldown_abilities) {
            c.tickAbilityCooldown(deltaTime);
        }
    }

    public void end() {
        this.deactivate();

        assert(Ability.active_abilities.contains(this)): "ability not found in active list";
        Ability.active_abilities.remove(this);
        Ability.cooldown_abilities.add(this);
    }

    public void tickAbilityDuration(float deltaTime) {
        this.duration_left -= deltaTime;
        if (this.duration_left <= 0) { //if the ability duration has run out
            this.end(); //trigger deactivation phase
            this.duration_left = this.max_duration; //reset duration
            this.resetTicker();
        }
    }

    public void tickAbilityCooldown(float deltaTime) {
        this.cooldown_left -= deltaTime;
        if (this.cooldown_left <= 0) { //if the cooldown is over, we can use the ability again
            assert (Ability.cooldown_abilities.contains(this)): "ability not found in cooldown list";
            Ability.cooldown_abilities.remove(this);
            this.cooldown_left = this.max_cooldown; //reset cooldown
        }
    }

    //simple setters
    public void tickTicker() { this.ticker--; }
    public void resetTicker() { this.ticker = 1000000; }

    public static void dispose() {
        Ability.active_abilities.clear();
        Ability.cooldown_abilities.clear();
    }
}


class SwiftstrikeAbility extends Ability {

    private float dash_speed;
    private String slash_projectile;
    private String slash_pattern;

    public SwiftstrikeAbility() { }

    @Override public void activate() {
        if (this.name.equals("cherryblossom_twinblades")) {
            this.player.shoot(slash_projectile,this.player.getRotation(),this.slash_pattern,1,1);
            for (int i = 0; i < 4; i ++) {
                this.player.shoot(slash_projectile,this.player.getRotation()+90*i*MathUtils.degreesToRadians,"double",1,1);
            }

        }
    }

    @Override public void update() { //do a speed boost
        int impX = (int) (this.dash_speed * MathUtils.cos(this.player.getRotation()));
        int impY = (int) (this.dash_speed * MathUtils.sin(this.player.getRotation()));
        this.player.getBody().applyLinearImpulse(impX, impY, 0, 0, true);

        if (this.name.equals("simple_wakizashi")) {
            new Particle(player,"run_dust",1);
        }
        if (this.name.equals("whirlwind_sheath") && this.ticker%2 == 0) {
            this.player.shoot(slash_projectile,this.player.getRotation()-180*MathUtils.degreesToRadians,slash_pattern,1,1);
            new Particle(player,"run_dust",2);
        }


    }

    @Override public void deactivate() { //at the end of the dash, do a slash attack
        if (this.name.equals("simple_wakizashi")) {
            this.player.shoot(slash_projectile,this.player.getRotation(),this.slash_pattern,1,1);
        } else if (this.name.equals("shadowstep_dagger")) {
            this.player.shoot(slash_projectile,this.player.getRotation(),this.slash_pattern,1,1);
        } else if (this.name.equals("tsuinejji")) {
            float dist1 = 0.3f*Global.PPM;
            float dist2 = 0.4f*Global.PPM;
            float shoot_angle = 20*MathUtils.degreesToRadians;
            float a = player.getRotation();

            //left slashes
            Vector2 ice_pos1 = new Vector2(player.getX()+dist1*MathUtils.cos(a+MathUtils.PI/2),player.getY()+dist1*MathUtils.sin(a+MathUtils.PI/2));
            Vector2 ice_pos2 = new Vector2(player.getX()+dist2*MathUtils.cos(a+MathUtils.PI/2),player.getY()+dist2*MathUtils.sin(a+MathUtils.PI/2));
            Vector2 fire_pos1 = new Vector2(player.getX()+dist1*MathUtils.cos(a-MathUtils.PI/2),player.getY()+dist1*MathUtils.sin(a-MathUtils.PI/2));
            Vector2 fire_pos2 = new Vector2(player.getX()+dist2*MathUtils.cos(a-MathUtils.PI/2),player.getY()+dist2*MathUtils.sin(a-MathUtils.PI/2));

            this.player.newProjectile("tsuinejji_ice_slash",a-shoot_angle,1,1,ice_pos1);
            this.player.newProjectile("tsuinejji_ice_slash",a-shoot_angle,1,1,ice_pos2);
            this.player.newProjectile("tsuinejji_fire_slash",a+shoot_angle,1,1,fire_pos1);
            this.player.newProjectile("tsuinejji_fire_slash",a+shoot_angle,1,1,fire_pos2);

        }
    }

}

class WarcryAbility extends Ability {
    private float radius;
    private String active_effect;
    private String affected_entites;

    public WarcryAbility() { }

    @Override public void activate() {
        for (Player p : Global.game.getPlayerList()) {
            if (Math.hypot(p.getX()-this.player.getX(),p.getY()-this.player.getY()) > this.radius) { continue; }

            if (this.affected_entites.equals("team") && this.player.getTeamtag() != p.getTeamtag()) { continue; }
            else if (this.affected_entites.equals("enemies") && this.player.getTeamtag() == p.getTeamtag()) { continue; }
            else if (this.affected_entites.equals("all")) { } //place holder just to show that the option is here
            else if (this.affected_entites.equals("self") && !player.equals(p)) { continue; }

            //apply effects
            if (this.active_effect.equals("burning")) {
                p.applyActiveEffect(active_effect,max_duration);
            } else if (this.active_effect.equals("speedy")) {
                p.applyActiveEffect(this.active_effect,max_duration);
            } else if (this.active_effect.equals("purify")) {

            } else if (this.active_effect.equals("repel")) {

            }

        }
    }

    @Override public void update() { //all players within a certain radius get a buff/debuff
    }

    @Override public void deactivate() {

    }

}

class QuiverAbility extends Ability{

    private String quiver_projectile;
    private String quiver_pattern;

    public QuiverAbility() { }

    @Override public void activate() {
        if (this.name.equals("simple_quiver")) {
            this.player.shoot(quiver_projectile,player.getRotation(),quiver_pattern,1,1);
        } else if (this.name.equals("devastation_quiver")) {
            this.player.shoot(quiver_projectile, player.getRotation(), quiver_pattern, 1, 1);
        }
     }

    @Override public void update() { //all players within a certain radius get a buff/debuff

    }

    @Override public void deactivate() {

    }
}

class SpellAbility extends Ability{

    String spell_projectile;
    String spell_pattern;

    public SpellAbility() { }

    @Override public void activate() {

    }

    @Override public void update() {
        if (this.name.equals("flamethrower_scroll") && this.ticker%4 == 0) {
            float dist = 0.15f*Global.PPM;
            for (int i = 0; i < 2; i++) {
                float angle = player.getRotation() + (-12 + Global.rnd.nextInt(24)) * MathUtils.degreesToRadians;
                Vector2 pos = new Vector2(player.getX() + dist * MathUtils.cos(angle), player.getY() + dist * MathUtils.sin(angle));
                player.newProjectile(spell_projectile, angle, 1, 1, pos);
            }
        }
    }

    @Override public void deactivate() {

    }
}


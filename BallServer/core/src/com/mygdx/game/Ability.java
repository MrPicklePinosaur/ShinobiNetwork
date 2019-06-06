package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;

import java.util.LinkedList;

public abstract class Ability {
    protected static LinkedList<Ability> active_abilities = new LinkedList<Ability>(); //list of abilities current being used
    protected static LinkedList<Ability> cooldown_abilities = new LinkedList<Ability>(); //list of abbilities currently under cooldown

    protected String name;
    protected float max_duration;
    protected float max_cooldown;
    protected Player player;

    protected float duration_left; //more accurate way of recording how much time left in ability
    protected float cooldown_left;
    protected int ticker; //counts how many ticks (less accurate, but easy to divide)

    public Ability() { }
    public Ability(String name,float max_duration,float max_cooldown) {
        this.name = name;
        this.max_duration = max_duration;
        this.max_cooldown = max_cooldown;

        this.duration_left = this.max_duration;
        this.cooldown_left = this.max_cooldown;
        this.resetTicker();
    }

    public void bind_player(Player player) { this.player = player; }

    public abstract void activate(); //what to do when ability is activated
    public abstract void update(); //what to do when ticking ability
    public abstract void deactivate(); //what to do when ability is deactivated

    public void begin() {
        assert (this.player != null): "Player hasn't been binded yet!!";
        //you cant activate ability if its already activated or under cool down
        if (Ability.cooldown_abilities.contains(this) || Ability.active_abilities.contains(this)) { return; }

        Ability.active_abilities.add(this);

        this.activate();
    }

    public static void updateAll(float deltaTime) {
        for (Ability a : Ability.active_abilities) {
            a.update();
            a.tickAbilityDuration(deltaTime);
            a.tickTicker();
        }
        for (Ability c : Ability.cooldown_abilities) {
            c.tickAbilityCooldown(deltaTime);
        }
    }

    public void tickAbilityDuration(float deltaTime) {
        this.duration_left -= deltaTime;
        if (this.duration_left <= 0) {
            this.end();
            this.duration_left = this.max_duration;
            this.resetTicker();
        }
    }

    public void tickAbilityCooldown(float deltaTime) {
        this.cooldown_left -= deltaTime;
        if (this.cooldown_left <= 0) {
            assert (Ability.cooldown_abilities.contains(this)): "ability not found in cooldown list";
            Ability.cooldown_abilities.remove(this);
            this.cooldown_left = this.max_cooldown;
        }
    }

    public void end() {
        this.deactivate();

        assert(Ability.active_abilities.contains(this)): "ability not found in active list";
        Ability.active_abilities.remove(this);
        Ability.cooldown_abilities.add(this);
    }

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

    public void tickTicker() { this.ticker--; }
    public void resetTicker() { this.ticker = 1000000; }
}


class SwiftstrikeAbility extends Ability {

    private float dash_speed;
    private String slash_projectile;
    private String slash_pattern;

    public SwiftstrikeAbility() { }

    @Override public void activate() { }

    @Override public void update() { //do a speed boost
        int impX = (int) (this.dash_speed * MathUtils.cos(this.player.getMouseAngle()));
        int impY = (int) (this.dash_speed * MathUtils.sin(this.player.getMouseAngle()));
        this.player.getBody().applyLinearImpulse(impX, impY, 0, 0, true);

        if (this.name.equals("whirlwind") && this.ticker%2 == 0) {
            this.player.shoot(slash_projectile,this.player.getMouseAngle()-180*MathUtils.degreesToRadians,slash_pattern,1,1);
        }
    }

    @Override public void deactivate() { //at the end of the dash, do a slash attack
        if (this.name.equals("simple_wakizashi")) {
            this.player.shoot(slash_projectile,this.player.getMouseAngle(),this.slash_pattern,1,1);
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
            }

        }
    }

    @Override public void update() { //all players within a certain radius get a buff/debuff

    }

    @Override public void deactivate() {

    }

}

class QuiverAbility extends Ability{

    public QuiverAbility() { }

    @Override public void activate() {

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

    }

    @Override public void deactivate() {

    }
}
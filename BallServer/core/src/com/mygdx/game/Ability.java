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

    protected float duration_left;
    protected float cooldown_left;

    public Ability() { }
    public Ability(String name,float max_duration,float max_cooldown) {
        this.name = name;
        this.max_duration = max_duration;
        this.max_cooldown = max_cooldown;

        this.duration_left = this.max_duration;
        this.cooldown_left = this.max_cooldown;
    }

    public void bind_player(Player player) { this.player = player; }

    public abstract void activate(); //what to do when ability is activated
    public abstract void update(); //what to do when ticking ability
    public abstract void deactivate(); //what to do when ability is deactivated

    public void begin() {
        assert (this.player != null): "Player hasn't been binded yet!!";
        //you cant activate ability if its already activated or under cool down
        if (Ability.cooldown_abilities.contains(this) || Ability.active_abilities.contains(this)) { return; }

        this.activate();

        Ability.active_abilities.add(this);
    }

    public static void updateAll(float deltaTime) {
        for (Ability a : Ability.active_abilities) {
            a.update();
            a.tickAbilityDuration(deltaTime);
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
            System.out.println(AssetManager.getAbilityJsonData(abl_type,abl_name));
            newAbility = Global.json.fromJson(SwiftstrikeAbility.class,AssetManager.getAbilityJsonData(abl_type,abl_name));
        }

        assert (newAbility != null): "Failed to create ability";
        newAbility.bind_player(player);
        return newAbility;
    }
}


class SwiftstrikeAbility extends Ability {

    private float dash_speed;
    private String projectile_path;
    private Weapon slash_weapon;

    public SwiftstrikeAbility() { }
    public SwiftstrikeAbility(String name, float max_duration, float max_cooldown, float dash_speed) {
        super(name,max_duration,max_cooldown);
        this.dash_speed = dash_speed;
        //this.slash_weapon = new Weapon("empty.png",AssetManager.getWeaponJsonData(slash_weapon),this.player);
    }

    @Override public void activate() { }

    @Override public void update() { //do a speed boost
        int impX = (int)(this.dash_speed*MathUtils.cos(this.player.getMouseAngle()));
        int impY = (int)(this.dash_speed*MathUtils.sin(this.player.getMouseAngle()));
        this.player.getBody().applyLinearImpulse(impX,impY,0,0,true);
    }

    @Override public void deactivate() { //at the end of the dash, do a slash attack

    }

}
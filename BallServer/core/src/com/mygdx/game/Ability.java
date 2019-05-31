package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;

import java.util.LinkedList;

public class Ability {
    private static LinkedList<Ability> active_abilities = new LinkedList<Ability>();

    private Player player;
    private float max_duration;
    private float duration_left;

    private boolean activated;

    public Ability(Player player,float max_duration) {
        this.player = player;
        this.max_duration = max_duration;
        this.duration_left = this.max_duration;

        this.activated = false;
    }

    public void activate() {
        if (this.activated == true) { return; } //you cant activate ability if its already activated

        ABILITYTYPE ability = ABILITYTYPE.valueOf(this.player.stats.getAbility().toUpperCase());

        if (ability == ABILITYTYPE.DASH) {


        } else if (ability == ABILITYTYPE.WARCRY) {

        }

        this.activated = true;
        Ability.active_abilities.add(this);

    }

    public static void updateAll(float deltaTime) {
        for (Ability a : Ability.active_abilities) {
            a.updateAbility(deltaTime);
            System.out.println(a.duration_left);
        }
    }
    public void updateAbility(float deltaTime) {
        int impX = (int)(player.stats.getSpeed()*10*MathUtils.cos(this.player.getMouseAngle()));
        int impY = (int)(player.stats.getSpeed()*10*MathUtils.sin(this.player.getMouseAngle()));
        this.player.getBody().applyLinearImpulse(impX,impY,0,0,true);

        this.duration_left -= deltaTime;
        if (this.duration_left <= 0) {
            this.deactivate();
        }
    }

    public void deactivate() {
        if (this.activated == false) { return; } //you cant deactivate ability if its not activated

        this.activated = false;
        this.duration_left = this.max_duration;

        assert(Ability.active_abilities.contains(this)): "ability not found";
        Ability.active_abilities.remove(this);
    }



}

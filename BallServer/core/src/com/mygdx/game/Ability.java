package com.mygdx.game;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.LinkedList;

public class Ability {
    private static LinkedList<Ability> active_abilities = new LinkedList<Ability>();

    private Player player;
    private int duration_left;

    private boolean activated;

    public Ability(Player player,int duration) {
        this.player = player;
        this.duration_left = duration;

        this.activated = false;
    }

    public void activate() {
        if (!this.activated) { return; } //you cant activate ability if its already activated

        ABILITYTYPE ability = ABILITYTYPE.valueOf(this.player.stats.getAbility().toUpperCase());

        if (ability == ABILITYTYPE.DASH) {
            this.player.getBody().applyLinearImpulse(10,0,0,0,true);

        } else if (ability == ABILITYTYPE.WARCRY) {

        }

        this.activated = true;

    }

    public void updateAll() {
        for (Ability a : Ability.active_abilities) {
            a.updateAll();
        }
    }
    public void updateAbility() {

    }

    public void deactivate() {
        if (this.activated) { return; } //you cant deactivate ability if its not activated

        this.activated = false;
    }



}

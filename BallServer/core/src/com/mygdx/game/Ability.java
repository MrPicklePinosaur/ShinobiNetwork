package com.mygdx.game;

import com.badlogic.gdx.math.MathUtils;

import javax.swing.*;
import java.util.LinkedList;

public class Ability {
    private static LinkedList<Ability> active_abilities = new LinkedList<Ability>(); //list of abilities current being used
    private static LinkedList<Ability> cooldown_abilities = new LinkedList<Ability>(); //list of abbilities currently under cooldown

    private Player player;

    private float duration_left;
    private float cooldown_left;

    public Ability(Player player) {
        this.player = player;


        this.duration_left = this.player.stats.ability_stats.getDuration();
        this.cooldown_left = this.player.stats.ability_stats.getCooldown();
    }

    public void activate() {
        //you cant activate ability if its already activated or under cool down
        if (Ability.cooldown_abilities.contains(this) || Ability.active_abilities.contains(this)) { return; }

        if (player.stats.ability_stats.getName().equals("slash")) {

        } else if (player.stats.ability_stats.getName().equals("warcry")) {

        }

        Ability.active_abilities.add(this);
    }

    public static void updateAll(float deltaTime) {
        for (Ability a : Ability.active_abilities) {
            a.updateAbility();
            a.tickAbilityDuration(deltaTime);
        }
        for (Ability c : Ability.cooldown_abilities) {
            c.tickAbilityCooldown(deltaTime);
        }
    }

    public void updateAbility() {
        if (player.stats.ability_stats.getName().equals("slash")) {
            int impX = (int)(player.stats.getSpeed()*10*MathUtils.cos(this.player.getMouseAngle()));
            int impY = (int)(player.stats.getSpeed()*10*MathUtils.sin(this.player.getMouseAngle()));
            this.player.getBody().applyLinearImpulse(impX,impY,0,0,true);
        } else if (player.stats.ability_stats.getName().equals("warcry")) {

        }
    }
    public void tickAbilityDuration(float deltaTime) {
        this.duration_left -= deltaTime;
        if (this.duration_left <= 0) {
            this.deactivate();
            this.duration_left = player.stats.ability_stats.getDuration();;
        }
    }

    public void tickAbilityCooldown(float deltaTime) {
        this.cooldown_left -= deltaTime;
        if (this.cooldown_left <= 0) {
            assert (Ability.cooldown_abilities.contains(this)): "ability not found in cooldown list";
            Ability.cooldown_abilities.remove(this);
            this.cooldown_left = this.player.stats.ability_stats.getCooldown();
        }
    }

    public void deactivate() {
        if (player.stats.ability_stats.getName().equals("slash")) {

        } else if (player.stats.ability_stats.getName().equals("warcry")) {

        }

        assert(Ability.active_abilities.contains(this)): "ability not found in active list";
        Ability.active_abilities.remove(this);
        Ability.cooldown_abilities.add(this);
    }
}


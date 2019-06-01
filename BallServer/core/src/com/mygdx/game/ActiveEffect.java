package com.mygdx.game;

import java.util.LinkedList;

public class ActiveEffect {
    private LinkedList<ActiveEffect> active_effects = new LinkedList<ActiveEffect>();

    private Player player;
    private String name;
    private float max_duration;

    private float duration_left;

    public ActiveEffect(Player player,String name,float max_duration) {
        this.player = player;
        this.name = name;
        this.max_duration = max_duration;

        this.duration_left = max_duration;
    }

    public void begin() {

    }

    public static void updateAll() {

    }
    public void update() {

    }

    public void end() {

    }

}
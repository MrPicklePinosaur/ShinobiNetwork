package com.mygdx.game;

import java.util.LinkedList;

public class ActiveEffect {
    private Player player;
    private String name;
    private float max_duration;

    private float duration_left;

    public ActiveEffect(Player player,String name,float max_duration) {
        this.player = player;
        this.name = name;
        this.max_duration = max_duration;

        this.resetDurationTimer();
    }

    public void begin() {

    }

    public static void updateAll(float deltaTime) {
        for (Player p : Global.game.getPlayerList()) {
            for (ActiveEffect a : p.getActiveEffectsList().values()) {
                a.update();
                a.tickEffectDuration(deltaTime);
            }
        }
    }

    public void update() {
        System.out.println("ouch that burns "+duration_left);
    }

    public void tickEffectDuration(float deltaTime) {
        this.duration_left -= deltaTime;
        if (this.duration_left <= 0) {
            this.end();
            this.resetDurationTimer();
        }
    }

    public void end() {
        this.player.removeEffect(this.name);
    }

    public void resetDurationTimer() {
        this.duration_left = this.max_duration;
    }

}
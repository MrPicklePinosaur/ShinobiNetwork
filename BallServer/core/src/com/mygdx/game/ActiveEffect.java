package com.mygdx.game;

import java.util.LinkedList;

public class ActiveEffect {
    private Player player;
    private String name;
    private float max_duration;

    private float duration_left;
    private int ticker;

    public ActiveEffect(Player player,String name,float max_duration) {
        this.player = player;
        this.name = name;
        this.max_duration = max_duration;

        this.resetDurationTimer();
        this.resetTicker();
    }

    public void begin() {
        if (this.name.equals("speedy")) {
            player.setSpeed(player.getSpeed()*2);
        } else if (this.name.equals("burning")) {
            new Particle(player,"burning",(int)max_duration);
        } else if (this.name.equals("frostbite")) {
            new Particle(player, "frostbite", (int) max_duration);
        } else if (this.name.equals("frozen")) {
            player.setSpeed(player.getSpeed()/2);
        }
    }

    public static void updateAll(float deltaTime) {
        for (Player p : Global.game.getPlayerList()) {
            for (ActiveEffect a : p.getActiveEffectsList().values()) {
                a.update();
                a.tickEffectDuration(deltaTime);
                a.tickTicker();
            }
        }
    }

    public void update() {
        if (this.name.equals("burning") && ticker%6 == 0) {
            this.player.modHp(-0.15f);
        } else if (this.name.equals("frostbite") && ticker%6 == 0) {
            this.player.modHp(-0.5f);
        }

    }

    public void tickEffectDuration(float deltaTime) {
        this.duration_left -= deltaTime;
        if (this.duration_left <= 0) {
            this.end();
            this.resetDurationTimer();
            this.resetTicker();
        }
    }

    public void end() {
        if (this.name.equals("speedy")) {
            player.setSpeed(player.stats.getSpeed());
        } else if (this.name.equals("frozen")) {
            player.setSpeed(player.stats.getSpeed());
        }
        this.player.removeEffect(this.name);
    }

    public float getDurationLeft() { return this.duration_left; }
    public void resetDurationTimer() {
        this.duration_left = this.max_duration;
    }

    public void tickTicker() { this.ticker--; }
    public void resetTicker() { this.ticker = 1000000; }

}
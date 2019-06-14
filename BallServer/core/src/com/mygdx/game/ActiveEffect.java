/* Shinobi Network
 ______   ______   ______  __   __   __ ______       ______   ______  ______  ______   ______   ______
/\  __ \ /\  ___\ /\__  _\/\ \ /\ \ / //\  ___\     /\  ___\ /\  ___\/\  ___\/\  ___\ /\  ___\ /\__  _\
\ \  __ \\ \ \____\/_/\ \/\ \ \\ \ \'/ \ \  __\     \ \  __\ \ \  __\\ \  __\\ \  __\ \ \ \____\/_/\ \/
 \ \_\ \_\\ \_____\  \ \_\ \ \_\\ \__|  \ \_____\    \ \_____\\ \_\   \ \_\   \ \_____\\ \_____\  \ \_\
  \/_/\/_/ \/_____/   \/_/  \/_/ \/_/    \/_____/     \/_____/ \/_/    \/_/    \/_____/ \/_____/   \/_/

  Handles buffs and debuffs
 */

package com.mygdx.game;

public class ActiveEffect {
    private Player player;
    private String name;
    private float max_duration;

    //variables used for updates
    private float duration_left;
    private int ticker;

    public ActiveEffect(Player player,String name,float max_duration) {
        this.player = player;
        this.name = name;
        this.max_duration = max_duration;

        this.resetDurationTimer();
        this.resetTicker();
    }

    public void begin() { //init phase
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


    //seimple setters
    public float getDurationLeft() { return this.duration_left; }
    public void resetDurationTimer() {
        this.duration_left = this.max_duration;
    }
    public void tickTicker() { this.ticker--; }
    public void resetTicker() { this.ticker = 1000000; }

}
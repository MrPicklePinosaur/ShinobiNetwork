package com.mygdx.game;

import com.badlogic.gdx.math.Vector3;

public class UserStats {

    private int kills;
    private int deaths;
    private float dmg_dealt;

    public UserStats() {
        this.kills = 0;
        this.deaths = 0;
        this.dmg_dealt = 0;
    }

    public void reset_performance_stats() { //used when the game resets
        this.kills = 0;
        this.deaths = 0;
        this.dmg_dealt = 0;
    }

    //stat setters
    public void addKill() { this.kills++; }
    public void addDeath() { this.deaths++; }
    public void addDmgDealt(float dmg_dealt) { this.dmg_dealt+= dmg_dealt; }

    //stat getters
    //possibly remove all the individual getters
    public int getKills() { return this.kills; }
    public int getDeaths() { return this.deaths; }
    public float getDmgDealt() { return this.dmg_dealt; }
    public Vector3 getGameStats() { return new Vector3(this.kills,this.deaths,this.dmg_dealt); }



}

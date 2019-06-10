package com.mygdx.game;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;

public class Weapon extends Entity {

    private Entity owner;
    public WeaponStats stats;

    public Weapon(String name, String json_stat_data, Entity owner) {
        super(name);
        this.owner = owner;

        this.entity_type = ET.WEAPON;

        this.init_stats(json_stat_data);
    }


    @Override
    public void init_stats(String json_data) { this.stats = Global.json.fromJson(WeaponStats.class, json_data); }

    @Override public float getX() { return this.owner.getX(); }
    @Override public float getY() { return this.owner.getY(); }
    @Override public float getRotation() { return this.owner.getRotation(); }
}

class WeaponStats {
    private String name;
    private String weapon_type;
    private String projectile;
    private String fire_type;
    private float fire_rate; //fire rate is the cooldown time between two concecutive attacks
    private String fire_pattern;
    private float time_to_charge;

    public WeaponStats() { }

    public String getName() { return this.name; }
    public String getWeaponType() { return this.weapon_type; }
    public String getProjectileName() { return this.projectile; }
    public String getFireType() { return this.fire_type; }
    public float getFireRate() { return this.fire_rate; }
    public String getFirePattern() { return this.fire_pattern; }
    public float getTimeToCharge() { return this.time_to_charge; }
}


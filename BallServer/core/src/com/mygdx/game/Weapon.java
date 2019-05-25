package com.mygdx.game;

import com.badlogic.gdx.utils.Json;

public class Weapon extends Entity {

    private Entity owner;
    public WeaponStats stats;

    public Weapon(String texture_path, String json_stat_data, Entity owner) {
        super(texture_path);
        this.owner = owner;

        this.entity_type = ET.WEAPON;

        this.init_stats(json_stat_data);
    }


    @Override
    public void init_stats(String json_data) {
        this.stats = Global.json.fromJson(WeaponStats.class, json_data);
    }

    @Override
    public float getX() {
        return this.owner.getX();
    }

    @Override
    public float getY() {
        return this.owner.getY();
    }

    @Override
    public float getRotation() {
        return this.owner.getRotation();
    }
}

class WeaponStats {
    private String name;
    private String projectile_path;
    private float fire_rate;

    public WeaponStats() { }
    public WeaponStats(String name,String projectile_path,float fire_rate) {
        this.name = name;
        this.projectile_path = projectile_path;
        this.fire_rate = fire_rate;
    }

    public String getName() { return this.name; }
    public String getProjectilePath() { return this.projectile_path; }
    public float getFireRate() { return this.fire_rate; }
}


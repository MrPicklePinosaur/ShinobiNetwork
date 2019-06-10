package com.mygdx.game;

class WeaponInfo {

    String name;
    String weapon_type;
    String projectile;
    String fire_type;
    String fire_pattern;
    String time_to_charge;

    public WeaponInfo() { }
}

class ProjectileInfo {

    private String name;
    private float damage;
    private float bullet_speed;
    private float max_dist;
    private int penetration;
    private String travel_pattern;

    public ProjectileInfo() { }
}

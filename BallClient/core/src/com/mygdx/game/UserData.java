package com.mygdx.game;

public class UserData {

    String username;
    int total_kills;
    int total_deaths;
    int total_damage;
    String[] inventory;

    public UserData() { }

    public static UserData init_client(String json_data) { return Global.json.fromJson(UserData.class, json_data); }

    public String getUsername() { return this.username; }
    public int getTotalKills() { return this.total_kills; }
    public int getTotalDeaths() { return this.total_deaths; }
    public int getTotalDamage() { return this.total_damage; }
    public String[] getInventory() { return this.inventory; }
}

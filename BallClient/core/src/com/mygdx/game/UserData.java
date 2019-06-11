package com.mygdx.game;

public class UserData {

    String username;
    int total_kills;
    int total_deaths;
    int total_damage;
    String[] inventory;
    String[] ninja_loadout;
    String[] archer_loadout;
    String[] warrior_loadout;
    String[] wizard_loadout;

    public UserData() { }

    public static UserData init_client(String json_data) { return Global.json.fromJson(UserData.class, json_data); }

    public String getUsername() { return this.username; }
    public int getTotalKills() { return this.total_kills; }
    public int getTotalDeaths() { return this.total_deaths; }
    public int getTotalDamage() { return this.total_damage; }
    public String[] getInventory() { return this.inventory; }

    public String[] getLoadout(String filter) {
        if (filter.equals("ninja")) { return this.ninja_loadout; }
        else if (filter.equals("archer")) { return this.archer_loadout; }
        else if (filter.equals("warrior")) { return this.warrior_loadout; }
        else if (filter.equals("wizard")) { return this.wizard_loadout; }
        return new String[]{};
    }
}

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

    public String setLoadout(String item_name,String item_type) {
        if (item_type.equals("katana")) {
            this.ninja_loadout[0] = item_name; return "ninja";
        } else if (item_type.equals("waki")) {
            this.ninja_loadout[1] = item_name; return "ninja";
        } else if (item_type.equals("bow")) {
            this.archer_loadout[0] = item_name; return "archer";
        } else if (item_type.equals("quiver")) {
            this.archer_loadout[1] = item_name; return "archer";
        } else if (item_type.equals("sword")) {
            this.warrior_loadout[0] = item_name; return "warrior";
        } else if (item_type.equals("helm")) {
            this.warrior_loadout[1] = item_name; return "warrior";
        } else if (item_type.equals("staff")) {
            this.wizard_loadout[0] = item_name; return "wizard";
        } else if (item_type.equals("spell")) {
            this.wizard_loadout[1] = item_name; return "wizard";
        }
        return "";
    }


}

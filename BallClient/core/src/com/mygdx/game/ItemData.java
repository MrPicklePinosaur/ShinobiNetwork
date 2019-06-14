package com.mygdx.game;

//Used for item descirptions and backpack item sorting
public class ItemData {
    String id; //used for id with server
    String display_name;
    String item_type;
    String stat_text;
    String special_text;

    public ItemData() { }

    public static ItemData init_itemdata(String json_data) { return Global.json.fromJson(ItemData.class, json_data); }

    public String getDisplayName() { return this.display_name; }
    public String getItemType() { return this.item_type; }
    public String getStatText() { return this.stat_text; }
    public String getSpecialText() { return this.special_text; }
}

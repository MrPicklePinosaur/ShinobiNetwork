package com.mygdx.game;

public class ItemData {
    String id;
    String display_name;
    String item_type;
    String stat_text;
    String special_text;

    public ItemData() { }

    public static ItemData init_itemdata(String json_data) { return Global.json.fromJson(ItemData.class, json_data); }
}

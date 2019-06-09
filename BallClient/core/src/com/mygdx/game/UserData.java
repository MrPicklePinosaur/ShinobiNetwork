package com.mygdx.game;

public class UserData {

    String username;
    String[] inventory;

    public UserData() { }

    public static UserData init_client(String json_data) { return Global.json.fromJson(UserData.class, json_data); }

    public String getUsername() { return this.username; }
}

package com.mygdx.game;

import java.util.ArrayList;

public class Client {

    String username;
    String[] inventory;

    public Client() { }

    public static Client init_client(String json_data) { return Global.json.fromJson(Client.class, json_data); }

    public String getUsername() { return this.username; }
}

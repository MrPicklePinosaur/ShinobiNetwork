//shrey mahey
package com.mygdx.game;

import java.sql.*;
import java.util.HashMap;

public class Database {
    private HashMap<String,String> password_list = new HashMap<String, String>();
    private HashMap<String,String> data_list = new HashMap<String, String>();

    private Connection c;
    private Statement stmt;

    public Database(){
        //trying to connect to DB
        try{
            Class.forName("org.sqlite.JDBC");
            //System.out.println(System.getProperty("user.dir"));
            c = DriverManager.getConnection("jdbc:sqlite:playersDB.db");  //if malfunctioning, check working folder location -- this file SHOULD be checking in root folder, not assets
            //connection url is currently relative, but must be tracked if the database, this file, or any related directory in between is moved/changed
            System.out.println("Successfully connected to DB.");

        } catch (Exception ex){ System.out.println("Database constructor error: "+ex); }
        refreshData();
    }

    public void refreshData(){
            try {
                this.stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM players");
                while(rs.next()){
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String data = rs.getString("data");

                    if (!this.password_list.containsKey(username)) { this.password_list.put(username,password); }
                    if (!this.data_list.containsKey(username)) { this.data_list.put(username,data); }
                }
            } catch(Exception ex){ System.out.println("getData method error: "+ex); }
    }

    public void closeConnection() {
        try { c.close(); }
        catch (Exception ex) { System.out.println("closeConnection method error: " + ex); }
    }

    public boolean checkCredentials(String username,String password){
        //if the user exists and the password matches
        if (this.password_list.containsKey(username) && this.password_list.get(username).equals(password)) {
            return true;
        }
        return false;
    }

    public String getPassword(String username) {
        assert (this.password_list.containsKey(username)): "User not found";
        return this.password_list.get(username);
    }
    public String getData(String username) {
        assert (this.data_list.containsKey(username)): "User not found";
        return this.data_list.get(username);
    }

    /*
    public void writeToDB(String username,String password,String json) {

        try {
            this.stmt = c.createStatement();
            if (this.data_list.containsKey(username)){ //if the username already exists, update it
                String updateData = "UPDATE players SET data = "+json+" WHERE username = "+username;
                stmt.executeUpdate(updateData);
            } else { //otherwise create a new entry
                String playerInfo = "INSERT INTO players (username,password,data) VALUES ('"+username+"','"+password+"','"+json+"')";
                stmt.executeUpdate(playerInfo);
            }
        } catch (SQLException ex) { System.out.println("writeTODB error: "+ex); }
    }
    */

    public boolean new_user(String username,String password) {
        try {
            this.stmt = c.createStatement();
            if (this.data_list.containsKey(username)) { return false; } //if the username already exists, update it
            // otherwise create a new entry
            String empty_json = getDefaultJson(username);
            String playerInfo = "INSERT INTO players (username,password,data) VALUES ('"+username+"','"+password+"','"+empty_json+"')";
            stmt.executeUpdate(playerInfo);

            refreshData(); //make sure to reload all data when we modify the database
        } catch (SQLException ex) { System.out.println("writeTODB error: "+ex); }
        return true;
    }

    public String getDefaultJson(String username) {

        String default_inv = "[\"kazemonji\",\"simple_wakizashi\"]";
        String default_ninja = "[\"kazemonji\",\"simple_wakizashi\"]";
        String default_archer = "[]";
        String default_warrior = "[]";
        String default_wizard = "[]";

        String defaultJson =
                "{\n"+
                "\"username\": \""+username+"\",\n"+
                "\"total_kills\": 0,\n"+
                "\"total_deaths\": 0,\n"+
                "\"total_damage\": 0,\n"+
                "\"inventory\": "+default_inv+",\n"+
                        "\"ninja_loadout\": "+default_ninja+",\n"+
                        "\"archer_loadout\": "+default_archer+",\n"+
                        "\"warrior_loadout\": "+default_inv+",\n"+
                        "\"wizard_loadout\": "+default_inv+",\n"+
                "}";
        return defaultJson;
    }


}

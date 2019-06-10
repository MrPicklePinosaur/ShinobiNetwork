//shrey mahey
package com.mygdx.game;

import com.badlogic.gdx.utils.JsonValue;

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
        loadAllData();
    }

    public void loadAllData(){
            try {
                this.stmt = c.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT * FROM players");
                while(rs.next()){
                    int id = rs.getInt("id");
                    String username = rs.getString("username");
                    String password = rs.getString("password");
                    String data = rs.getString("data");

                    this.password_list.put(username,password);
                    this.data_list.put(username,data);
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

            //INSERT JSON OBJECT!!!!
            String playerInfo = "INSERT INTO players (username,password,data) VALUES ('"+username+"','"+password+"',)";
            stmt.executeUpdate(playerInfo);
            return true;

        } catch (SQLException ex) { System.out.println("writeTODB error: "+ex); }

    }


}

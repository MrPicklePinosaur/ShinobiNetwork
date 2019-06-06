//shrey mahey
package com.mygdx.game;

import sun.awt.image.ImageWatched;

import java.sql.*;
import java.util.HashMap;
import java.util.LinkedList;

public class Database {
    private Connection c;
    private Statement stmt;

    public Database(){
        //trying to connect to DB
        try{
            Class.forName("org.sqlite.JDBC");
            System.out.println(System.getProperty("user.dir"));
            c = DriverManager.getConnection("jdbc:sqlite:../../../playersDB.db");  //if malfunctioning, check working folder location -- this file SHOULD be checking in root folder, not assets
            //connection url is currently relative, but must be tracked if the database, this file, or any related directory in between is moved/changed
            System.out.println("Connected to DB.");

        }catch (Exception e){
            System.out.println("Database constructor error: "+e);
        }
    }

    public HashMap<String,LinkedList<String>> getPlayers(){
        HashMap<String,LinkedList<String>> players = new HashMap<String,LinkedList<String>>();
        try {
            this.stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM players");
            while(rs.next()){
                int id = rs.getInt("id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String data = rs.getString("data");
                LinkedList<String> relatedData = new LinkedList<String>();
                relatedData.add(password);
                relatedData.add(data);
                players.put(username,relatedData);
                //System.out.println(username+" "+password+" "+data);
            }
        }catch(Exception e){
            System.out.println("getPlayers method error: "+e);
        }
        return players;
    }   //close list of players

    public LinkedList<String> getUsernames() {
        LinkedList<String> usernames = new LinkedList<String>();
        try {
            this.stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT username FROM players");
            while (rs.next()) {
                String username = rs.getString("username");
                usernames.add(username);
            }
        } catch (Exception e) {
            System.out.println("getPlayers method error: " + e);
        }
        return usernames;
    }

    public void closeConnection() {
        try {
            c.close();
        } catch (Exception e) {
            System.out.println("closeConnection method error: " + e);
        }
    }

    public void checkCredentials(String username,String password){
        if(getPlayers().containsKey(username) && getPlayers().get(username).contains(password)){
            //Log in successfully
        }else{
            //'Your username or password is incorrect.'
        }
    }

    public void addPlayer(String u, String p){
        try{
            if(getPlayers().containsKey(u)){
                //we don't want the same username in the database
                //LogInUI.userNameTaken();
                //ERROR RUNNING
            }else {
                this.stmt = c.createStatement();
                String json = "[]";
                String playerInfo = "INSERT INTO players (username,password,data) VALUES ('"+u+"','"+p+"','"+json+"')";
                stmt.executeUpdate(playerInfo);
                /*
                For INSERT, UPDATE or DELETE
                use the executeUpdate() method
                and for SELECT
                use the executeQuery() method,
                which returns the ResultSet.
                */
                System.out.println("added player to list");
            }
        }catch(Exception e){
            System.out.println("addPlayer method error: "+e);
        }
    }
}

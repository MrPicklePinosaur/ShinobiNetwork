package com.mygdx.game;

import java.sql.*;

public class Database {

    public static void connect(String databasePath) {
        String filepath = "jdbc:sqlite:"+databasePath;
        Connection con = null;
        try {
            con = DriverManager.getConnection(filepath);
            System.out.println("Successfully connected to database");
        } catch (SQLException ex) { System.out.println(ex); }
    }


}


package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class Leaderboard {
    //Leaderboard keeps track of 4 things:
    //Kills, Deaths, Damage Dealt, and the associated Username
    static Table table;
    public Leaderboard(){
        table = new Table();
    }
}

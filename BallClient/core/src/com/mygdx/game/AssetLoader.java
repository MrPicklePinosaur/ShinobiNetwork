package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.*;
import java.util.*;

public class AssetLoader {
    public static HashMap<String, TextureRegion[]> animation_lib = new HashMap<String, TextureRegion[]>();

    public static void loadAnimations(String lib_filepath) { //loads all spreadsheets and converts them into Animation objects
        try { //the lib holds the filepaths of all the spritesheets
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(lib_filepath)));
            while (fileReader.hasNext()) {
                String filepath = fileReader.nextLine();
                Texture spritesheet = new Texture(filepath);

                int NUM_COLS = spritesheet.getWidth() / Global.SPRITESIZE;
                int NUM_ROWS = spritesheet.getHeight() / Global.SPRITESIZE;

                TextureRegion[][] raw = TextureRegion.split(spritesheet, Global.SPRITESIZE, Global.SPRITESIZE);
                TextureRegion[] frames = new TextureRegion[NUM_COLS * NUM_ROWS];
                //push all raw data into a 1D array
                int index = 0;
                for (int i = 0; i < NUM_ROWS; i++) {
                    for (int j = 0; j < NUM_COLS; j++) {
                        frames[index++] = raw[i][j];
                    }
                }
                AssetLoader.animation_lib.put(filepath, frames);
            }
        } catch (IOException ex) { System.out.println(ex); }
    }

    public static TextureRegion[] getAnimation(String file_path) {
        TextureRegion[] ani = AssetLoader.animation_lib.get(file_path);
        return ani.clone();
    }


}

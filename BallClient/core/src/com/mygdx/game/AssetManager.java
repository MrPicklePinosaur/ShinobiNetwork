package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;

import java.io.*;
import java.util.*;

public class AssetManager {
    public static HashMap<String, Texture> animation_lib = new HashMap<String, Texture>();

    public static void loadAnimations(String lib_filepath) { //loads all spreadsheets and converts them into Animation objects
        try { //the lib holds the filepaths of all the spritesheets
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(lib_filepath)));
            while (fileReader.hasNext()) {
                String filepath = fileReader.nextLine();
                Texture spritesheet = new Texture(filepath);

                AssetManager.animation_lib.put(filepath,spritesheet);
            }
            fileReader.close();
        } catch (IOException ex) { System.out.println(ex); }
    }

    public static Texture getSpritesheet(String file_path) {
        return AssetManager.animation_lib.get(file_path);
    }


}

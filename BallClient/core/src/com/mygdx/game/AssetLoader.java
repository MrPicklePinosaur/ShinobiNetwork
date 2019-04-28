package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.io.*;
import java.util.*;

public class AssetLoader {
    public static HashMap<String, Texture> animation_lib = new HashMap<String, Texture>();

    public static void loadAnimations(String lib_filepath) { //loads all spreadsheets and converts them into Animation objects
        try { //the lib holds the filepaths of all the spritesheets
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(lib_filepath)));
            while (fileReader.hasNext()) {
                String filepath = fileReader.nextLine();
                Texture spritesheet = new Texture(filepath);

                AssetLoader.animation_lib.put(filepath,spritesheet);
            }
        } catch (IOException ex) { System.out.println(ex); }
    }

    public static Texture getSpritesheet(String file_path) {
        return AssetLoader.animation_lib.get(file_path);

    }


}

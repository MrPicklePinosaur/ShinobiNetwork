/*
 ______   ______   ______   ______  ______     __    __   ______   __   __   ______   ______   ______   ______
/\  __ \ /\  ___\ /\  ___\ /\  ___\/\__  _\   /\ "-./  \ /\  __ \ /\ "-.\ \ /\  __ \ /\  ___\ /\  ___\ /\  == \
\ \  __ \\ \___  \\ \___  \\ \  __\\/_/\ \/   \ \ \-./\ \\ \  __ \\ \ \-.  \\ \  __ \\ \ \__ \\ \  __\ \ \  __<
 \ \_\ \_\\/\_____\\/\_____\\ \_____\ \ \_\    \ \_\ \ \_\\ \_\ \_\\ \_\\"\_\\ \_\ \_\\ \_____\\ \_____\\ \_\ \_\
  \/_/\/_/ \/_____/ \/_____/ \/_____/  \/_/     \/_/  \/_/ \/_/\/_/ \/_/ \/_/ \/_/\/_/ \/_____/ \/_____/ \/_/ /_/

 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;

import java.io.*;
import java.util.*;

public class AssetManager {
    public static HashMap<String, Texture> animation_lib = new HashMap<String, Texture>();
    public static HashMap<String, Texture> ui_lib = new HashMap<String, Texture>();

    public static void load_all() {
        loadFromDirectory("sprites/",AssetManager.animation_lib);
        loadFromDirectory("ui/",AssetManager.ui_lib);
    }

    public static void loadFromDirectory(String path,HashMap<String,Texture> target) {
        FileHandle root = new FileHandle(path);
        assert (root.isDirectory()): "Invalid directory"; //checks to see if input is acc a directory

        for (FileHandle file : root.list()) {
            if (file.isDirectory()) { AssetManager.loadFromDirectory(path+file.name()+"/",target); } //if we find a folder, go there
            if (file.name().contains(".png")) {
                String filepath = path+file.name();
                String name = file.name().replaceAll("\\.png","");
                target.put(name,new Texture(Gdx.files.internal(filepath)));
            }
        }
    }

    public static Texture getSpritesheet(String name) {
        assert (AssetManager.animation_lib.containsKey(name)): name+" not found";
        return AssetManager.animation_lib.get(name);
    }
    public static Texture getUIImage(String name) {
        assert (AssetManager.ui_lib.containsKey(name)): name+" not found";
        return AssetManager.ui_lib.get(name);
    }

}

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Scanner;

public class SoundPlayer {

    public static HashMap<String, Sound> sound_lib = new HashMap<String, Sound>();
    public static float sound_volume = 1f;

    public static void play_sound(String sound_name) {
        assert (SoundPlayer.sound_lib.containsKey(sound_name)): "Sound not found: "+sound_name;
        SoundPlayer.sound_lib.get(sound_name).play(sound_volume);
    }

    public static void load_sounds(String filepath) {
        try {
            Scanner fileReader = new Scanner(new BufferedReader(new FileReader(filepath)));

            while (fileReader.hasNext()) {
                String filename = fileReader.nextLine();
                SoundPlayer.sound_lib.put(filename, Gdx.audio.newSound(Gdx.files.internal("sounds/"+filename+".ogg")));
            }

        } catch (FileNotFoundException ex) { System.out.println(ex); }
    }
}

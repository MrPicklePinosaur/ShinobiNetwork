/*
 ______   ______   ______   ______  ______     __    __   ______   __   __   ______   ______   ______   ______
/\  __ \ /\  ___\ /\  ___\ /\  ___\/\__  _\   /\ "-./  \ /\  __ \ /\ "-.\ \ /\  __ \ /\  ___\ /\  ___\ /\  == \
\ \  __ \\ \___  \\ \___  \\ \  __\\/_/\ \/   \ \ \-./\ \\ \  __ \\ \ \-.  \\ \  __ \\ \ \__ \\ \  __\ \ \  __<
 \ \_\ \_\\/\_____\\/\_____\\ \_____\ \ \_\    \ \_\ \ \_\\ \_\ \_\\ \_\\"\_\\ \_\ \_\\ \_____\\ \_____\\ \_\ \_\
  \/_/\/_/ \/_____/ \/_____/ \/_____/  \/_/     \/_/  \/_/ \/_/\/_/ \/_/ \/_/ \/_/\/_/ \/_____/ \/_____/ \/_/ /_/

 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class AssetManager { //mainly just a bunch of helper methods
    private static LinkedList<Body> kill_list = new LinkedList<Body>(); //list of bodies to be safely destroyed
    private static HashMap<Entity, Vector3> move_list = new HashMap<Entity, Vector3>(); //list of bodies to be safely moved

    //json libraries
    private static HashMap<String,String> player_stats = new HashMap<String, String>();
    private static HashMap<String,String> weapon_stats = new HashMap<String, String>();
    private static HashMap<String,HashMap<String,String>> ability_stats = new HashMap<String, HashMap<String, String>>();

    //helper methods for bodies
    public static Body createBody(FixtureDef fdef, BodyDef.BodyType bodyType) { //takes in a fixture and creates a body
        BodyDef bdef = new BodyDef();
        bdef.type = bodyType;
        assert (Global.world != null): "world has not been initialized";
        Body new_body = Global.world.createBody(bdef);
        new_body.createFixture(fdef);
        return new_body;
    }

    public static void flagForPurge(Body body) { AssetManager.kill_list.add(body); }
    public static void sweepBodies() { //removes all bodies safely
        for (Body b : AssetManager.kill_list) {
            assert(b != null): "Body you are trying to purge is null";
            Global.world.destroyBody(b);
            b.setUserData(null);
            b = null;
        }
        AssetManager.kill_list.clear();
    }

    public static void flagForMove(Entity e,Vector3 v) { AssetManager.move_list.put(e,v); }
    public static void moveBodies() { //moves all bodies safely
        for (Entity e : AssetManager.move_list.keySet()) {
            assert(e != null): "Body you are trying to move is null";
            Vector3 v = AssetManager.move_list.get(e);
            e.init_pos(v.x/Global.PPM,v.y/Global.PPM,v.z);
        }
        AssetManager.move_list.clear();
    }

    public static void load_all_json() {
        AssetManager.load_json(AssetManager.player_stats,"json/base_player_stats.json");
        AssetManager.load_json(AssetManager.weapon_stats,"json/base_weapon_stats.json");
        AssetManager.load_ability_json(AssetManager.ability_stats,"json/ability_stats.json");
    }

    private static void load_json(HashMap<String,String> lib,String filepath) { //takes in a library and populates it with the json of the file at filepath
        JsonReader json = new JsonReader();
        JsonValue raw_json = json.parse(Gdx.files.internal(filepath));

        JsonValue cur = raw_json.child;
        while (true) {
            //System.out.println(cur.name+"\n"+cur);
            //System.out.println(cur.getString("name")+"\n"+cur.toString());
            lib.put(cur.getString("name"),cur.toString());
            if (cur.next == null) { break; }
            cur = cur.next;
        }
    }
    private static void load_ability_json(HashMap<String,HashMap<String,String>> lib,String filepath) {
        JsonReader json = new JsonReader();
        JsonValue raw_json = json.parse(Gdx.files.internal(filepath));

        JsonValue abl_type = raw_json.child.child;
        while (true) { //iterate through ability types

            HashMap<String,String> abls = new HashMap<String, String>(); //stores all abilities of one type

            JsonValue abl = abl_type.child;
            while (true) { //iterate through abilities
                abls.put(abl.getString("name"),abl.toString());

                if (abl.next == null) { break; }
                abl = abl.next;
            }

            lib.put(abl_type.name,abls);
            if (abl_type.next == null) { break; }
            abl_type = abl_type.next;
        }
    }

    public static String getPlayerJsonData(String key) {
        assert(AssetManager.player_stats.containsKey(key)): "Player stats not found!";
        return AssetManager.player_stats.get(key);
    }
    public static String getWeaponJsonData(String key) {
        assert(AssetManager.weapon_stats.containsKey(key)): "Weapon stats not found!";
        return AssetManager.weapon_stats.get(key);
    }
    public static String getAbilityJsonData(String abl_type,String abl_name) {
        assert(AssetManager.ability_stats.containsKey(abl_type)): "Invalid ability type";
        assert(AssetManager.ability_stats.get(abl_type).containsKey(abl_name)): "Invalid ability";
        return AssetManager.ability_stats.get(abl_type).get(abl_name);
    }

}

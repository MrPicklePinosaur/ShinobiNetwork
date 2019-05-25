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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;

public class AssetManager { //mainly just a bunch of helper methods

    //json libraries
    private static HashMap<String,String> player_stats = new HashMap<String, String>();
    private static HashMap<String,String> projectile_stats = new HashMap<String, String>();

    private LinkedList<Body> kill_list = new LinkedList<Body>(); //list of bodies to be safely destroyed
    private HashMap<Entity, Vector3> move_list = new HashMap<Entity, Vector3>(); //list of bodies to be safely moved
    private Game game;

    public AssetManager(Game game) {
        this.game = game;
    }
    //helper methods for bodies
    public Body createBody(FixtureDef fdef, BodyDef.BodyType bodyType) { //takes in a fixture and creates a body
        BodyDef bdef = new BodyDef();
        bdef.type = bodyType;
        assert (this.game.world != null): "world has not been initialized";
        Body new_body = this.game.world.createBody(bdef);
        new_body.createFixture(fdef);
        return new_body;
    }

    public void flagForPurge(Body body) { this.kill_list.add(body); }
    public void sweepBodies() { //removes all bodies safely
        for (Body b : this.kill_list) {
            assert(b != null): "Body you are trying to purge is null";
            this.game.world.destroyBody(b);
            b.setUserData(null);
            b = null;
        }
        this.kill_list.clear();
    }

    public void flagForMove(Entity e,Vector3 v) { this.move_list.put(e,v); }
    public void moveBodies() { //moves all bodies safely
        for (Entity e : this.move_list.keySet()) {
            assert(e != null): "Body you are trying to move is null";
            Vector3 v = this.move_list.get(e);
            e.init_pos(v.x/Global.PPM,v.y/Global.PPM,v.z);
        }
        this.move_list.clear();
    }

    public static void load_all_json() {
        AssetManager.load_json(AssetManager.player_stats,"json/base_player_stats.json");
        AssetManager.load_json(AssetManager.projectile_stats,"json/base_projectile_stats.json");
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

    public static String getPlayerJsonData(String key) {
        assert(AssetManager.player_stats.containsKey(key)): "Player stats not found!";
        return AssetManager.player_stats.get(key);
    }
    public static String getProjectileJsonData(String key) {
        assert(AssetManager.projectile_stats.containsKey(key)): "Projectile stats not found!";
        return AssetManager.projectile_stats.get(key);
    }

}

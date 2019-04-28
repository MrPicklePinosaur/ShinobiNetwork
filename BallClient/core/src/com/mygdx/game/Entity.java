package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.concurrent.ConcurrentHashMap;

public class Entity {
    private static Entity client_entity; //the entity that this specific client owns
    private static int client_entity_id = -1;

    private static ConcurrentHashMap<Integer,Entity> entity_library = new ConcurrentHashMap<Integer,Entity>(); //used so we know which piece of data belongs to which entity

    private float x;
    private float y;
    private float rotation;

    private float old_x;
    private float old_y;
    private float old_rotation;

    private Animation<TextureRegion> animation;
    private float frameTime = 0.25f; //used for animation

    private Entity(String texture_path) { //THE ONLY TIME CLIENT IS ALLOWED TO CREATE ENTITIES IS IF THE SERVER SAYS SO

        TextureRegion[] frames = Entity.createAnimation(texture_path);
        this.animation = new Animation<TextureRegion>(frameTime,frames);

        this.x = 0; this.y = 0; this.rotation = 0;
        this.old_x = 0; this.old_y = 0; this.old_rotation = 0;
        this.frameTime = 1f;

    }

    private static TextureRegion[] createAnimation(String texture_path) {
        assert (AssetManager.animation_lib.containsKey(texture_path)): "Texture hasn't been loaded yet.";
        Texture spritesheet = AssetManager.getSpritesheet(texture_path);

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
        return frames;
    }

    public void update_pos(float new_x, float new_y, float new_rotation) {
        this.old_x = this.x; this.old_y = this.y; this.old_rotation = this.rotation;
        this.x = new_x; this.y = new_y; this.rotation = new_rotation;
    }

    public static void update_entity(String data) { //if entity doesnt exist, we create a new one
        //format: ID,texture_path,x,y
        String[] parsed = data.split(",");
        int id = Integer.parseInt(parsed[0]);
        String texture_path = parsed[1];
        float x = Float.parseFloat(parsed[2]);
        float y = Float.parseFloat(parsed[3]);
        float rot = Float.parseFloat(parsed[4]);

        Entity entity;
        if (!Entity.entity_library.containsKey(id)) { //if entity doesnt exist yet, create it
            //This block creates and integrates the entity
            Entity newEntity = new Entity(texture_path);
            entity_library.put(id,newEntity);

            if (id == Entity.client_entity_id) { Entity.client_entity = newEntity; }
            entity = newEntity;

        } else { entity = Entity.entity_library.get(id); }

        //apply all the updates
        entity.update_pos(x,y,rot);
    }

    public static void kill_entity(int id) {
        assert (Entity.entity_library.containsKey(id)): "The entity you are trying to remove doesn't exist in master list";
        Entity.entity_library.remove(id);
    }

    public void stepFrame(float deltaTime) { this.frameTime += deltaTime; } //possibly combine with getFrame
    public static void stepFrameAll(float deltaTime) {
        for (Entity e : Entity.entity_library.values()) {
            e.stepFrame(deltaTime);
        }
    }

    public TextureRegion getFrame() { return this.animation.getKeyFrame(this.frameTime,true); }

    public static void drawAll(SpriteBatch batch) {
        for (Entity e : Entity.entity_library.values()) {
            TextureRegion tex = e.getFrame();

            //TODO: dont generalise for players (only reflection in y-axis)
            if (e.getOldX()-e.getX() > 0 && !tex.isFlipX()) { tex.flip(true,false);} //PROBLEM: THIS AFFECTS THE STORED TEXTUREREGIONS, SO ALL ENTITIES WILL GET FLIPPED
            else if (e.getOldX()-e.getX() < 0 && tex.isFlipX()) { tex.flip(true,false);}
            batch.draw(tex,e.getX()-Global.SPRITESIZE/2,e.getY()-Global.SPRITESIZE/2); //TODO, add scaling and rotation ALSO, DONT ASSUME SPRITESIZE!!!!!
        }
    }

    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getOldX() { return this.old_x; }
    public float getOldY() { return this.old_y; }

    public static Entity getClientEntity() {
        //assert (Entity.client_entity != null): "Client_entity has not been initalized";
        return Entity.client_entity;
    }

    public static void assignClientId(int id) { Entity.client_entity_id = id; }

}

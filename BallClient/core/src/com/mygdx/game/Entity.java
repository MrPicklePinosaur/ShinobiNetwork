/*
 ______   __   __   ______  __   ______  __  __
/\  ___\ /\ "-.\ \ /\__  _\/\ \ /\__  _\/\ \_\ \
\ \  __\ \ \ \-.  \\/_/\ \/\ \ \\/_/\ \/\ \____ \
 \ \_____\\ \_\\"\_\  \ \_\ \ \_\  \ \_\ \/\_____\
  \/_____/ \/_/ \/_/   \/_/  \/_/   \/_/  \/_____/

 */

package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;

import java.util.EmptyStackException;
import java.util.concurrent.ConcurrentHashMap;

public class Entity {

    private static ConcurrentHashMap<Integer,Entity> entity_library = new ConcurrentHashMap<Integer,Entity>(); //used so we know which piece of data belongs to which entity

    private String entity_type;
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
        assert (AssetManager.animation_lib.containsKey(texture_path)): "Texture ("+texture_path+") hasn't been loaded yet.";
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
        String entity_type = parsed[0];
        int id = Integer.parseInt(parsed[1]);
        String texture_path = parsed[2];
        float x = Float.parseFloat(parsed[3]);
        float y = Float.parseFloat(parsed[4]);
        float rot = Float.parseFloat(parsed[5]);

        Entity entity;
        if (!Entity.entity_library.containsKey(id)) { //if entity doesnt exist yet, create it
            //This block creates and integrates the entity
            Entity newEntity = new Entity(texture_path);
            entity_library.put(id,newEntity);

            entity = newEntity;

        } else { entity = Entity.entity_library.get(id); }

        //apply all the updates
        entity.entity_type = entity_type;
        entity.update_pos(x,y,rot);
    }

    public static void kill_entity(int id) {
        //assert (Entity.entity_library.containsKey(id)): "The entity you are trying to remove doesn't exist in master list";
        if (!Entity.entity_library.containsKey(id)) { return; } //if the entity we are trying to remove doesnt exist, ignore it
        Entity.entity_library.remove(id);
    }

    public void stepFrame(float deltaTime) { this.frameTime += deltaTime; } //possibly combine with getFrame
    public static void stepFrameAll(float deltaTime) {
        for (Entity e : Entity.entity_library.values()) {
            e.stepFrame(deltaTime);
        }
    }

    public TextureRegion getFrame() { return this.animation.getKeyFrame(this.frameTime,true); }
    //public TextureRegion getIdleFrame() { return this.animation.getKeyFrame(0,true); } // for now, we just assume the first frame of the animation is the idle frame}

    public static void drawAll(SpriteBatch batch) {
        for (Entity e : Entity.entity_library.values()) {
            TextureRegion tex = e.getFrame();

            float rot = e.getRotation();
            if (rot < 0) rot += MathUtils.PI2; //get rid of negative angles
            //TODO: dont generalise for players (only reflection in y-axis)
            if (e.getET().equals(ET.PLAYER.toString())) {
                //if mouse is in 2nd or 3rd quadrant, face left
                if (MathUtils.PI/2 < rot && rot < MathUtils.PI*3/2 && !tex.isFlipX()) { tex.flip(true, false); }
                //if mouse is in 4th or 1st quadrant, face right
                else if ((MathUtils.PI*3/2 < rot || rot < MathUtils.PI/2) && tex.isFlipX()) { tex.flip(true, false); }
                batch.draw(tex,e.getX()-Global.SPRITESIZE/2,e.getY()-Global.SPRITESIZE/2); //TODO, add scaling and rotation ALSO, DONT ASSUME SPRITESIZE!!!!!
            } else if (e.getET().equals(ET.PROJECTILE.toString())) {
                batch.draw(tex,e.getX()-Global.SPRITESIZE/2,e.getY()-Global.SPRITESIZE/2,Global.SPRITESIZE/2,Global.SPRITESIZE/2,Global.SPRITESIZE,Global.SPRITESIZE,1,1,e.getRotation()* MathUtils.radiansToDegrees);
            } else if (e.getET().equals(ET.WEAPON.toString())) {
                //if mouse is in 2nd or 3rd quadrant, face left
                if (MathUtils.PI/2 < rot && rot < MathUtils.PI*3/2 && !tex.isFlipY()) { tex.flip(false, true); }
                //if mouse is in 4th or 1st quadrant, face right
                else if ((MathUtils.PI*3/2 < rot || rot < MathUtils.PI/2) && tex.isFlipY()) { tex.flip(false, true); }
                batch.draw(tex,e.getX()-Global.SPRITESIZE/2,e.getY()-Global.SPRITESIZE/2,Global.SPRITESIZE/2,Global.SPRITESIZE/2,Global.SPRITESIZE,Global.SPRITESIZE,Global.WEAPONSCALE,Global.WEAPONSCALE,e.getRotation()* MathUtils.radiansToDegrees);
            }
        }
    }

    public static Entity getEntity(int id) {
        assert (Entity.entity_library.containsKey(id)): "Entity not found";
        return Entity.entity_library.get(id);
    }
    public static Boolean isAlive(int id) {
        return Entity.entity_library.containsKey(id) ? true : false;
    }
    public String getET() { return this.entity_type; }
    public float getX() { return this.x; }
    public float getY() { return this.y; }
    public float getRotation() { return this.rotation; }
    public float getOldX() { return this.old_x; }
    public float getOldY() { return this.old_y; }
}

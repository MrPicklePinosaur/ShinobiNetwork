package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Entity {
    private static ConcurrentHashMap<Integer,Entity> entity_library = new ConcurrentHashMap<Integer,Entity>();

    private Sprite sprite;

    public Entity(String texture_path) {
        this.sprite = new Sprite(new Texture(texture_path));
    }

    public void set_pos(float x, float y, float rotation) {
        this.sprite.setX(x);
        this.sprite.setY(y);
        this.sprite.setRotation(rotation);
    }

    public static Entity getEntity(int id) {
        if (Entity.entity_library.containsKey(id)) {
            return Entity.entity_library.get(id);
        }
        return null;
    }

    public static void update_entity(String data) { //if entity doesnt exist, we create a new one
        //format: ID,texture_path,x,y
        String[] parsed = data.split(",");
        int id = Integer.parseInt(parsed[0]);
        String texture_path = parsed[1];
        float x = Float.parseFloat(parsed[2]);
        float y = Float.parseFloat(parsed[3]);

        Entity entity = Entity.getEntity(id);
        if (entity == null) { //if the entity doesnt exist, create add it
            Entity.entity_library.put(id,new Entity(texture_path));
        }
        entity.set_pos(x,y,0);

    }

    public static void draw_all(SpriteBatch batch) { //iterate through entity hashmap and draws everything
        for (Entity e : Entity.entity_library.values()) {
            e.getSprite().draw(batch);
        }
    }

    public Sprite getSprite() { return this.sprite; }
}

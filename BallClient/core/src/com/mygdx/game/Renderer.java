package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Renderer {

    private static ConcurrentLinkedQueue<Sprite> draw_queue = new ConcurrentLinkedQueue<Sprite>();

    public static void add(Sprite sprite) {
        draw_queue.add(sprite);
    }

    public static void drawAll(SpriteBatch batch) { //draws all queued sprites
        for (Sprite s : draw_queue) {
            s.draw(batch);
            draw_queue.remove(s);
        }
    }
}

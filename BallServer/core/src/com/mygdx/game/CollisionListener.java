package com.mygdx.game;

import com.badlogic.gdx.physics.box2d.*;
import javafx.util.Pair;

public class CollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        System.out.println(fa.getBody().getUserData().getClass().getName()+" "+fb.getBody().getUserData().getClass().getName());

         if (CollisionListener.fixtureMatch(fa,fb,Projectile.class,Map.class)) { //if projectile hits a wall
             Projectile p = (Projectile) findInstance(fa,fb,Projectile.class).getBody().getUserData();
             //remove prijecitle

         }

    }


    public static Boolean fixtureMatch(Fixture fa,Fixture fb,Class<?> cls1,Class<?> cls2) { //checks to see if two fixtures are two certain object types
        //checks to see if both of the fixtures are instances of the classes provided
        if (cls1.isInstance(fa.getBody().getUserData()) && cls2.isInstance(fb.getBody().getUserData())) return true;
        if (cls2.isInstance(fa.getBody().getUserData()) && cls1.isInstance(fb.getBody().getUserData())) return true;
        return false;
    }

    public static Fixture findInstance(Fixture fa,Fixture fb,Class<?> cls) { //finds out which of the two fixtures is the type you want
        if (cls.isInstance(fa.getBody().getUserData())) return fa;
        if (cls.isInstance(fb.getBody().getUserData())) return fb;
        return null;
    }


    @Override
    public void endContact(Contact c) {

    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) { }
    @Override public void postSolve(Contact contact, ContactImpulse impulse) { }
}

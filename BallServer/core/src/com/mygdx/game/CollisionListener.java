/*
 ______   ______   __       __       __   ______   __   ______   __   __       __       __   ______   ______  ______   __   __   ______   ______
/\  ___\ /\  __ \ /\ \     /\ \     /\ \ /\  ___\ /\ \ /\  __ \ /\ "-.\ \     /\ \     /\ \ /\  ___\ /\__  _\/\  ___\ /\ "-.\ \ /\  ___\ /\  == \
\ \ \____\ \ \/\ \\ \ \____\ \ \____\ \ \\ \___  \\ \ \\ \ \/\ \\ \ \-.  \    \ \ \____\ \ \\ \___  \\/_/\ \/\ \  __\ \ \ \-.  \\ \  __\ \ \  __<
 \ \_____\\ \_____\\ \_____\\ \_____\\ \_\\/\_____\\ \_\\ \_____\\ \_\\"\_\    \ \_____\\ \_\\/\_____\  \ \_\ \ \_____\\ \_\\"\_\\ \_____\\ \_\ \_\
  \/_____/ \/_____/ \/_____/ \/_____/ \/_/ \/_____/ \/_/ \/_____/ \/_/ \/_/     \/_____/ \/_/ \/_____/   \/_/  \/_____/ \/_/ \/_/ \/_____/ \/_/ /_/

 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import javafx.util.Pair;

public class CollisionListener implements ContactListener {

    @Override
    public void beginContact(Contact c) {
        Fixture fa = c.getFixtureA();
        Fixture fb = c.getFixtureB();

        assert(fa != null && fb != null): "collision between nulls";
        //System.out.println(fa.getBody().getUserData().getClass().getName()+" "+fb.getBody().getUserData().getClass().getName());

        Pair<Class<?>,Object> type_a = (Pair<Class<?>,Object>) fa.getBody().getUserData();
        Pair<Class<?>,Object> type_b = (Pair<Class<?>,Object>) fb.getBody().getUserData();

        //System.out.println(type_a.getKey()+" "+type_b.getKey());
        if (CollisionListener.fixtureMatch(type_a.getKey(),type_b.getKey(),Projectile.class, GameMap.class)) {
            Projectile b = (Projectile) CollisionListener.findFixture(type_a,type_b,Projectile.class);
            b.removeProjecitle();
        }

        else if (CollisionListener.fixtureMatch(type_a.getKey(),type_b.getKey(),Player.class,Projectile.class)) {
            final Player p = (Player) CollisionListener.findFixture(type_a,type_b,Player.class);
            final Projectile b = (Projectile) CollisionListener.findFixture(type_a,type_b,Projectile.class);

            final Player owner = (Player) b.getOwner();
            if ((p.getTeamtag() == TEAMTAG.SOLO || p.getTeamtag() != owner.getTeamtag()) && p != owner) { //if the player is allowed to be hit (aka no friendly fire)

                Gdx.app.postRunnable(new Runnable() { //TODO: HANDLE OWNER DYING WHILE STUFF IS HAPPENING
                    @Override
                    public void run() {
                        b.checkPenetration(); //check to see if bullet should die

                        //deal damage
                        float damage = b.getDamage();
                        b.hit_effect(p,damage); //apply special effects, if there are any
                        owner.performance.addDmgDealt(b.getDamage());

                        try {
                            if (p.modHp(-1*damage)) { //if the bullet killed the player
                                b.kill_effect(p); //apply special effects, if there are any

                                //update stats
                                Global.game.addKill(owner);
                                owner.performance.addKill();
                                p.performance.addDeath();
                            }
                        } catch (NullPointerException ex) { //if player dies and damage is still attmpting to be dealt
                            System.out.println("Nullpointer in collisionlistener");
                        }
                    }
                });

            }
        }


    }

    public static Boolean fixtureMatch(Class<?> cls_a, Class<?> cls_b, Class<?> cls1, Class<?> cls2) {
        if (cls_a == cls1 && cls_b == cls2) { return true; }
        if (cls_b == cls1 && cls_a == cls2) { return true; }
        return false;
    }

    public static Object findFixture(Pair<Class<?>,Object> type_a,Pair<Class<?>,Object> type_b,Class<?> cls) {
        if (type_a.getKey() == cls) { return type_a.getValue(); }
        if (type_b.getKey() == cls) { return type_b.getValue(); }
        return null;
    }



    @Override
    public void endContact(Contact c) {

    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) { }
    @Override public void postSolve(Contact contact, ContactImpulse impulse) { }
}

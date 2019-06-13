package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

public class InputHandler extends InputAdapter {

    @Override public boolean touchDown(int x, int y, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Global.server_socket.send_msg(MT.USIN,"MOUSE_LEFT_DOWN");
            return true;
        }
        return false;
    }

    @Override public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            Global.server_socket.send_msg(MT.USIN,"MOUSE_LEFT_UP");
            return true;
        }
        return false;
    }

    public void handleInput() { //takes in user input and sends to server
        String msg = "";

        if (Gdx.input.isKeyPressed(Input.Keys.W)) { msg+=(",Key_W"); }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) { msg+=(",Key_S"); }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) { msg+=(",Key_A"); }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) { msg+=(",Key_D"); }
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) { msg+=(",Key_SPACE"); }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (!Global.game.game_screen.isMenuVisible() && !Global.game.game_screen.isRespawnMenuVisible()) { Global.game.game_screen.show_menu(); }
            else { Global.game.game_screen.hide_menu(); }
        } //button to open shop menu
        if (msg.equals("")) return; //if there isnt any input, don't send a message
        msg = msg.substring(1); //get rid of extra comma in front

        Global.server_socket.send_msg(MT.USIN,msg);
    }

    public void sendMouse() {
        Global.server_socket.send_msg(MT.USIN,"MOUSE_ANGLE:"+Global.m_angle);
    }


}

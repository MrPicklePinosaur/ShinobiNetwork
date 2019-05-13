package com.mygdx.game;

import java.util.concurrent.CopyOnWriteArrayList;

public class Game {

    private static CopyOnWriteArrayList<String> chat_log = new CopyOnWriteArrayList<String>();

    public static void new_chat_msg(String msg) {
        chat_log.add(msg);
    }

}

package com.mygdx.game;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

//links the main thread with the clientHandler threads
public class DataManager {

    private static ConcurrentLinkedQueue<String> data_queue = new ConcurrentLinkedQueue<String>();


    public static void add_msg(String raw_msg) { //adds new raw messsage to queue
        DataManager.data_queue.add(raw_msg);
    }

    public static void step_server() { //reads all data sent by clients and interperates it
        for (String s : DataManager.data_queue) {
            DataManager.input_unpacker(s);
            DataManager.data_queue.remove(s);
        }
    }

    private static void input_unpacker(String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        if (msg[0].equals(Global.MT_USIN)) {
            //client_entity.handleInput(msg[1]);
        } else if (msg[0].equals(Global.MT_CHATMSG)) {

        } else if (msg[0].equals(Global.MT_CMD)) {

        }
        //TODO: ADD GENERIC UPDATE ENTITY MESSAGE TYPE
    }

}

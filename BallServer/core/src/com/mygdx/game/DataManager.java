package com.mygdx.game;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

//links the main thread with the clientHandler threads
public class DataManager {

    //private static ConcurrentLinkedQueue<String> data_queue = new ConcurrentLinkedQueue<String>();
    private static ConcurrentHashMap<BallClientHandler,String> data_queue = new ConcurrentHashMap<BallClientHandler, String>();

    public static void add_msg(BallClientHandler client_sock,String raw_msg) { //adds new raw messsage to queue
        DataManager.data_queue.put(client_sock,raw_msg);
    }

    public static void step_server() { //reads all data sent by clients and interperates it
        for (BallClientHandler s : DataManager.data_queue.keySet()) {
            DataManager.input_unpacker(s,DataManager.data_queue.get(s));
            DataManager.data_queue.remove(s);
        }
    }

    private static void input_unpacker(BallClientHandler client_sock,String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        if (msg[0].equals(Global.MT_USIN)) {
            Entity client_entity = Entity.getEntity(client_sock.get_client_entity());
            client_entity.handleInput(msg[1]);
        } else if (msg[0].equals(Global.MT_CHATMSG)) {

        } else if (msg[0].equals(Global.MT_CMD)) {

        }
        //TODO: ADD GENERIC UPDATE ENTITY MESSAGE TYPE
    }

}

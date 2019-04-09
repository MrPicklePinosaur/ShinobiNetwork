package com.mygdx.game;

import java.util.Arrays;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataManager {

    private static ConcurrentLinkedQueue<String> data_queue = new ConcurrentLinkedQueue<String>();

    public static void add_msg(String raw_msg) { //adds new raw messsage to queue
        DataManager.data_queue.add(raw_msg);
    }

    public static void step_server() { //reads all data sent by clients and interperates it
        for (String s : DataManager.data_queue) {
            DataManager.in_unpacker(s);
            DataManager.data_queue.remove(s);
        }
    }

    public static void in_unpacker(String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        System.out.println(Arrays.toString(msg));
        if (msg[0].equals(Global.MT_UPDATE)) {
            String[] pos = msg[1].split(" ");
            //System.out.println(Arrays.toString(pos));
            for (String s : pos) {
                Entity.update_entity(s);
            }

        }
    }




}

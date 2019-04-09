package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class BallClient {
    //heavy lifters
    private Socket client_sock;
    private PrintWriter outstream;
    private BufferedReader instream;

    //vars
    private String ip;
    private int port;

    public BallClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public void start_connection() {
        try { //init stuff
            client_sock = new Socket(this.ip, this.port);
            outstream = new PrintWriter(client_sock.getOutputStream(), true);
            instream = new BufferedReader(new InputStreamReader(client_sock.getInputStream()));
        } catch (IOException ex) { System.out.println(ex); }

        //thread that listens for messages from server
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String server_msg = "";
                    while (true) {
                        server_msg = instream.readLine();
                        //System.out.println(server_msg);
                        in_unpacker(server_msg);
                    }
                } catch(IOException ex) { System.out.println(ex); }

            }
        }).start();
    }

    private void send_msg(String msg) { //send message to server
        outstream.println(msg);
    }


    public void close_connection() {
        try {
            outstream.close();
            instream.close();
            client_sock.close();
        } catch(IOException ex) { System.out.println(ex); }
    }

    public void out_packer(int msg_type,String msg) {
        if (msg_type == Global.MT_USIN) { //if the message we want to send is a user input
            this.send_msg("MT_USIN$"+msg);
        } else if (msg_type == Global.MT_CHATMSG) {

        } else if (msg_type == Global.MT_CMD) { 
            
        }
        
    }
    public void in_unpacker(String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        System.out.println(Arrays.toString(msg));
        if (msg[0].equals(Global.MT_UPDATE)) {
            /*
            String[] pos = msg[1].split(" ");
            //System.out.println(Arrays.toString(pos));
            for (String s : pos) {
                Entity.update_entity(s);
            }
            */
        }
    }


}

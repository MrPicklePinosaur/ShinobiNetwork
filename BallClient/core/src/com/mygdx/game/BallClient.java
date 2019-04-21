package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

import java.util.*;
import java.io.*;
import java.net.*;

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
                    while (true) {
                        final String server_msg = instream.readLine();

                        System.out.println(server_msg);

                        //interperate server message and post to rendering thread
                        Gdx.app.postRunnable(new Runnable() {

                            @Override
                            public void run() {
                                in_unpacker(server_msg);
                            }
                        });

                    }
                } catch(IOException ex) { System.out.println(ex); }

            }
        }).start();
    }

    public void close_connection() {
        try {
            outstream.close();
            instream.close();
            client_sock.close();
        } catch(IOException ex) { System.out.println(ex); }
    }

    //Can be used from anywhere in the main thread to send messages
    public void send_msg(int msg_type,String msg) { //send message to server
        String raw_msg = this.out_packer(msg_type,msg);
        outstream.println(raw_msg);
    }

    private String out_packer(int msg_type,String msg) { //helper method that 'encodes' message
        String data = null;
        if (msg_type == Global.MT_USIN) { //if the message we want to send is a user input
            data = ("MT_USIN$"+msg);
        } else if (msg_type == Global.MT_CHATMSG) {

        } else if (msg_type == Global.MT_CMD) {

        }
        assert (data != null): "empty message";
        return data;
    }

    public static void in_unpacker(String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        if (msg[0].equals(Global.MT_UPDATE)) {
            String[] pos = msg[1].split(" ");
            //System.out.println(Arrays.toString(pos));
            for (String s : pos) {
                Entity.update_entity(s);
            }
        } else if (msg[0].equals(Global.MT_KILLENTITY)) {
            int id = Integer.parseInt(msg[1]);
            Entity.kill_entity(id);
        }
    }

}

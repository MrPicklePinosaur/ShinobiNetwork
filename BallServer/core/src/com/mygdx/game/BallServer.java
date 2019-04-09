package com.mygdx.game;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

class BallServer {
    //heavy lifters
    private ServerSocket server_sock;

    //vars
    private int port;

    public BallServer(int port) {
        this.port = port;
    }

    public void start_server() { //boots up server
        try {
            server_sock = new ServerSocket(this.port);
        } catch(IOException ex) { System.out.println(ex); }
    }

    public void close_server() {
        try {
            server_sock.close();
        } catch(IOException ex) { System.out.println(ex); }
    }

    public ServerSocket getServerSocket() { return this.server_sock; }
}

class BallClientHandler {
    //heavy lifters
    private Socket client_sock;
    private PrintWriter outstream;
    private BufferedReader instream;

    //TODO: REMOVE CLIENTS FROM LIST WHEN THEY DC
    private static CopyOnWriteArrayList<BallClientHandler> client_list = new CopyOnWriteArrayList<BallClientHandler>(); //list of all clients

    public BallClientHandler(Socket client_sock) {
        this.client_sock = client_sock;
        client_list.add(this);
    }

    public void start_connection() { //inits stuff
        try {
            outstream = new PrintWriter(client_sock.getOutputStream(),true);
            instream = new BufferedReader(new InputStreamReader(client_sock.getInputStream()));
        } catch(IOException ex) { System.out.println(ex); }

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String client_msg = "";
                    while(true) {
                        client_msg = instream.readLine();
                        DataManager.add_msg(client_msg);
                        System.out.println(client_msg);

                    }
                } catch(IOException ex) { System.out.println(ex); }

            }
        }).start();
    }

    public void close_connection() {
        try {
            instream.close();
            outstream.close();
            client_sock.close();
            //TODO: REMOVE CLIENT FROM MASTER LIST HERE (possibly)
        } catch(IOException ex) { System.out.println(ex); }
    }


    public static void broadcast(int msg_type,String msg) { //sends a message to all connected clients
        for (BallClientHandler c : BallClientHandler.client_list) { //for each client thats connected, send this message
            c.send_msg(msg_type,msg);
        }
    }
    //THis can be used from anywhere in the main thread
    public void send_msg(int msg_type,String msg) {
        String raw_msg = output_packer(msg_type,msg);
        outstream.println(raw_msg);
    }

    private String output_packer(int msg_type, String msg) { //helper method that 'encodes' message
        String data = null;
        if (msg_type == Global.MT_UPDATE) { //tell client the position of all entites
            data = ("MT_UPDATE$"+msg);
        }
        assert (data == null); //if sm went wrong
        return data;
    }

}



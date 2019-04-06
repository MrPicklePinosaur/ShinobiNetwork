package com.mygdx.game;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

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
    private Entity entity;

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
            this.init_entity();
        } catch(IOException ex) { System.out.println(ex); }

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String client_msg = "";
                    while(true) {
                        client_msg = instream.readLine();

                        //System.out.println(client_msg);
                        //Interperate client message
                        input_unpacker(client_msg);
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

    private void send_msg(String msg) { outstream.println(msg); }

    public static void broadcast(int msg_type, String msg) { //sends a message to all connected clients
        for (BallClientHandler c : BallClientHandler.client_list) { //for each client thats connected, send this message
            c.output_packer(msg_type,msg);
        }
    }

    public void output_packer(int msg_type, String msg) {
        if (msg_type == Global.MT_UPDATE) { //tell client the position of all players
            this.send_msg("MT_UPDATE$"+msg);
        }
    }

    public void input_unpacker(String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        if (msg[0].equals(Global.MT_USIN)) {
            entity.handleInput(msg[1]);
        } else if (msg[0].equals(Global.MT_CHATMSG)) {

        } else if (msg[0].equals(Global.MT_CMD)) {
            
        }
    }

    public void init_entity() { //reads user stats from data base and creates an entity
        this.entity = new Entity(0,0);
    }
}



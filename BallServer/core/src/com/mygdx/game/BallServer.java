package com.mygdx.game;

import java.util.*;
import java.io.*;
import java.net.*;

class BallServer {
    //heavy lifters
    private ServerSocket server_sock;

    //vars
    private int port;

    public BallServer(int port) {
        this.port = port;
    }

    public void connect_clients() { //waits for clients to connect
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

class BallClientHandler implements Runnable {
    //heavy lifters
    private Socket client_sock;
    private PrintWriter outstream;
    private BufferedReader instream;
    private Entity entity;

    public BallClientHandler(Socket client_sock) {
        this.client_sock = client_sock;
        new Thread(this).start(); //auto-starts thread
    }

    @Override
    public void run() { //main thread stuff
        this.start_connection();
        this.init_entity();
        try {
            String client_msg = "";
            while(true) {
                client_msg = instream.readLine(); //read msg from client
                //System.out.println(client_msg);
                //Interperate client message
                this.input_unpacker(client_msg);

                //send client msg

            }
        } catch(IOException ex) { System.out.println(ex); }
    }

    public void start_connection() { //inits stuff
        try {
            outstream = new PrintWriter(client_sock.getOutputStream(),true);
            instream = new BufferedReader(new InputStreamReader(client_sock.getInputStream()));
        } catch(IOException ex) { System.out.println(ex); }
    }

    public void close_connection() {
        try {
            instream.close();
            outstream.close();
            client_sock.close();
        } catch(IOException ex) { System.out.println(ex); }
    }

    public void output_packer(int msg_type) {

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



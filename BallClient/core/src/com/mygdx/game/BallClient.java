package com.mygdx.game;

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

    public void start_connection() { //bascially an init method
        try {
            client_sock = new Socket(this.ip, this.port);
            outstream = new PrintWriter(client_sock.getOutputStream(), true);
            instream = new BufferedReader(new InputStreamReader(client_sock.getInputStream()));
        } catch (IOException ex) { System.out.println(ex); }
    }

    public void send_msg(String msg) { //send message to server
        outstream.println(msg);
    }

    /*
    public String listen() { //recieves messages from server

    }
    */

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
    public void in_unpacker() {

    }
}

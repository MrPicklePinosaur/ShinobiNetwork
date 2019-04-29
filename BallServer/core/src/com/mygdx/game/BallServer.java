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
    private BallClientHandler self;

    private static CopyOnWriteArrayList<BallClientHandler> client_list = new CopyOnWriteArrayList<BallClientHandler>(); //list of all clients

    private Player client_entity; //used so we know which entity belongs to client

    public BallClientHandler(Socket client_sock) {
        this.client_sock = client_sock;
        client_list.add(this);
    }

    public void start_connection() { //inits stuff
        try {
            outstream = new PrintWriter(client_sock.getOutputStream(),true);
            instream = new BufferedReader(new InputStreamReader(client_sock.getInputStream()));
        } catch(IOException ex) { System.out.println(ex); }

        init_client_entity();

        this.send_msg(MT.ASSIGNENTITY,""+this.client_entity.getId());
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String client_msg = "";
                    while(true) {
                        client_msg = instream.readLine(); //also include the entity id in the msg
                        //System.out.println(client_msg);

                        //interperate client message
                        input_unpacker(client_entity,client_msg);

                    }
                } catch(IOException ex) { //if something weird happens (including the client normally leaving game) disconnect the client
                    System.out.println("CLIENT HAS DISCONNECTED");
                    //first of all, send a message to the client telling them they dced

                    //tell entity to stop drawing it
                    broadcast(MT.KILLENTITY,""+client_entity.getId());

                    AssetManager.flagForPurge(client_entity.getBody()); //flag entity body for removal
                    Entity.removeEntity(client_entity); //remove client entity from list
                    removeClient();

                    //tie off some loose ends
                    close_connection();
                }

            }
        }).start();
    }

    public void close_connection() {
        try {
            instream.close();
            outstream.close();
            client_sock.close();
        } catch(IOException ex) { System.out.println(ex); }
    }

    public static void broadcast(MT msg_type,String msg) { //sends a message to all connected clients
        for (BallClientHandler c : BallClientHandler.client_list) { //for each client thats connected, send this message
            c.send_msg(msg_type,msg);
        }
    }
    //THis can be used from anywhere in the main thread
    public void send_msg(MT msg_type,String msg) {
        String raw_msg = output_packer(msg_type,msg);
        outstream.println(raw_msg);
    }

    private String output_packer(MT msg_type, String msg) { //helper method that 'encodes' message
        String data = null;
        if (msg_type == MT.UPDATE) { //tell client the position of all entites
            data = (MT.UPDATE+"$"+msg);
        } else if (msg_type == MT.KILLENTITY) { //tell client to remove client from their render queue
            data = (MT.KILLENTITY+"$"+msg); //in this case, msg is the entity id
        } else if (msg_type == MT.ASSIGNENTITY) { //tells client which entity they own when the connect
            data = (MT.ASSIGNENTITY+"$"+msg); //msg is the entity id
        } else if (msg_type == MT.LOADMAP) {
            data = (MT.LOADMAP+"$"+msg); //msg is the filepath of the map image
        }
        assert (data != null): "Empty data packet or invalid message type"; //if sm went wrong
        return data;
    }

    private static void input_unpacker(Player client_entity,String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        if (msg[0].equals(MT.USIN.toString())) {
            client_entity.handleInput(msg[1]);
        } else if (msg[0].equals(MT.CHATMSG.toString())) {

        } else if (msg[0].equals(MT.CMD.toString())) {

        }
        //TODO: ADD GENERIC UPDATE ENTITY MESSAGE TYPE
    }

    public void init_client_entity() {
        String texture_path = "ninja_run.png";
        this.client_entity = new Player(texture_path);
        this.client_entity.init_pos((float)100/Global.PPM,(float)100/Global.PPM,0);
    }

    public void removeClient() {
        BallClientHandler.client_list.remove(this);
    }


}



/*
 ______   ______   ______   __   __ ______   ______
/\  ___\ /\  ___\ /\  == \ /\ \ / //\  ___\ /\  == \
\ \___  \\ \  __\ \ \  __< \ \ \'/ \ \  __\ \ \  __<
 \/\_____\\ \_____\\ \_\ \_\\ \__|  \ \_____\\ \_\ \_\
  \/_____/ \/_____/ \/_/ /_/ \/_/    \/_____/ \/_/ /_/

 */

package com.mygdx.game;

import java.util.*;
import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;

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

        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("ClientHandler");

                try {
                    String client_msg = "";
                    while(true) {
                        client_msg = instream.readLine(); //also include the entity id in the msg
                        //System.out.println(client_msg);

                        //interperate client message
                        input_unpacker(client_msg);

                        send_msg(MT.BINDCAM,client_entity.getX()+","+client_entity.getY()); //TODO: prob a bad idea to put this here

                    }
                } catch(IOException ex) { //if something weird happens (including the client normally leaving game) disconnect the client
                    System.out.println("CLIENT HAS DISCONNECTED");
                    Global.game.new_chat_msg("CLIENT HAS DISCONNECTED");

                    //first of all, send a message to the client telling them they dced

                    //tell entity to stop drawing it
                    broadcast(MT.KILLENTITY,""+client_entity.getId());

                    AssetManager.flagForPurge(client_entity.getBody()); //flag entity body for removal
                    Entity.removeEntity(client_entity); //remove client entity from list

                    Entity.removeEntity(client_entity.getWeapon()); //remove the player's weapon

                    removeClient();
                    Global.game.removePlayer(client_entity);

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
        switch(msg_type) {
            case UPDATE: //tell client the position of all entites
                data = (MT.UPDATE+"$"+msg); break;
            case KILLENTITY: //tell client to remove client from their render queue
                data = (MT.KILLENTITY+"$"+msg); break; //in this case, msg is the entity id
            case LOADMAP:
                data = (MT.LOADMAP+"$"+msg); break; //msg is the filepath of the map image
            case SENDCHAT:
                data = (MT.SENDCHAT+"$"+msg); break; //msg is text_colour,msg
            case BINDCAM:
                data = (MT.BINDCAM+"$"+msg); break; //amsg is an x and y value of where the camera should be at
        }
        assert (data != null): "empty message";
        return data;
    }

    private void input_unpacker(String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        if (msg[0].equals(MT.USIN.toString())) {
            this.client_entity.handleInput(msg[1]);
        } else if (msg[0].equals(MT.CHATMSG.toString())) {
            Global.game.new_chat_msg(msg[1]);
        } else if (msg[0].equals(MT.CMD.toString())) {
            String[] cmd_msg = msg[1].split(" ");
            execute_command(cmd_msg);
        }
        //TODO: ADD GENERIC UPDATE ENTITY MESSAGE TYPE
    }

    public void init_client_entity() {
        String player_class = "ninja";
        TEAMTAG team = Global.game.chooseTeam();

        this.client_entity = new Player(player_class,AssetManager.getPlayerJsonData(player_class),team);
        Vector2 spawn_point = Global.map.get_spawn_point(this.client_entity.getTeamtag());
        this.client_entity.init_pos(spawn_point.x/Global.PPM,spawn_point.y/Global.PPM,0);
        Global.game.addPlayer(this.client_entity);
    }

    public void execute_command(String[] command) {
        //a command consists of the command name,followed by what you want to do with the command
        //if (command.length != 2) { return; }
        if ((command[0].toUpperCase()).equals(COMMANDS.TELEPORT.toString()) && command.length == 3) { //command consists of command name, x and y
            float x = this.client_entity.getX(); float y = this.client_entity.getY();
            try {
                x = Float.parseFloat(command[1]);
                y = Float.parseFloat(command[2]);
            } catch (NumberFormatException ex) {
                this.send_msg(MT.SENDCHAT,"[INVALID PARAMETERS FOR TELEPORT]"); return;
            }
            AssetManager.flagForMove(this.client_entity, new Vector3(x, y, this.client_entity.getRotation()));
        } else if ((command[0].toUpperCase()).equals(COMMANDS.HELP.toString())) {
            this.send_msg(MT.SENDCHAT, "[List of commands - help, teleport]");
        } else if ((command[0].toUpperCase()).equals(COMMANDS.SPEEDY.toString())) {
            this.client_entity.setSpeed(client_entity.getSpeed()*2);
        } else {
            this.send_msg(MT.SENDCHAT,"[INVALID COMMAND, for a comprehensive list of commands, try /help]");
        }

    }
    public void removeClient() {
        BallClientHandler.client_list.remove(this);
    }


}



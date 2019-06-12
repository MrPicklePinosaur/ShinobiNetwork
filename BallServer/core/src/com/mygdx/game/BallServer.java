/*
 ______   ______   ______   __   __ ______   ______
/\  ___\ /\  ___\ /\  == \ /\ \ / //\  ___\ /\  == \
\ \___  \\ \  __\ \ \  __< \ \ \'/ \ \  __\ \ \  __<
 \/\_____\\ \_____\\ \_\ \_\\ \__|  \ \_____\\ \_\ \_\
  \/_____/ \/_____/ \/_/ /_/ \/_/    \/_____/ \/_/ /_/

 */

package com.mygdx.game;

import java.io.*;
import java.net.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

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
    private static CopyOnWriteArrayList<BallClientHandler> client_list = new CopyOnWriteArrayList<BallClientHandler>(); //list of all clients

    //heavy lifters
    private Socket client_sock;
    private PrintWriter outstream;
    private BufferedReader instream;
    private BallClientHandler self;

    private Player client_entity; //used so we know which entity belongs to client
    private TEAMTAG teamtag;
    UserStats performance;
    private boolean game_in_progress = false; //if the user is actually in game, we send tell them what the heck is going on during the game

    public BallClientHandler(Socket client_sock) {
        this.client_sock = client_sock;
        client_list.add(this);
    }

    public void start_connection() { //inits stuff
        try {
            outstream = new PrintWriter(client_sock.getOutputStream(),true);
            instream = new BufferedReader(new InputStreamReader(client_sock.getInputStream()));
        } catch(IOException ex) { System.out.println(ex); }

        //init_client_entity();
        this.performance = new UserStats();

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

                        if (!game_in_progress) { continue; } //everything under here is stuff that can only be done if player is in game

                        if (client_entity == null) { //if the player is not alive
                            Vector2 default_camera_point = Global.map.getDefaultCameraPoint(); //if they arent, lock to a default point
                            send_msg(MT.BINDCAM,default_camera_point.x+","+default_camera_point.y);
                        } else { //if they are alive, send lock camera to their pos
                            send_msg(MT.BINDCAM,client_entity.getX()+","+client_entity.getY());
                        }
                    }
                } catch(IOException ex) { //if something weird happens (including the client normally leaving game) disconnect the client
                    System.out.println("CLIENT HAS DISCONNECTED");
                    Global.game.new_chat_msg("CLIENT HAS DISCONNECTED");

                    //first of all, send a message to the client telling them they dced
                    removePlayerFromGame();
                    close_connection();
                }


            }
        }).start();
    }

    public void killPlayer() {
        AssetManager.flagForPurge(this.client_entity.getBody()); //flag entity body for removal
        Entity.removeEntity(this.client_entity); //remove client entity from list
        Entity.removeEntity(this.client_entity.getWeapon()); //remove the player's weapon
        Global.game.removePlayer(this.client_entity);
        this.client_entity = null;
        if (isGameInProgress()) { send_msg(MT.CHOOSECLASS,""); } //if the player is still in game ask to choose new class to play as now
    }

    public void removePlayerFromGame () { //used when the player dcs
        //tell entity to stop drawing it
        if (client_entity != null) { //there is a chance that an entity was never inited
            killPlayer();
        }

        //tie off some loose ends
        removeClient();
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
            if (c.isGameInProgress()) { c.send_msg(msg_type,msg); }
        }
    }
    //THis can be used from anywhere in the main thread
    public void send_msg(MT msg_type,String msg) {
        String raw_msg = output_packer(msg_type,msg);
        outstream.println(raw_msg);
    }

    private String output_packer(MT msg_type, String msg) { //helper method that 'encodes' message
        String data = null;

        if (msg_type == MT.CREDSACCEPTED) { //with message
            data = MT.CREDSACCEPTED+"$"+msg;
        } else if (msg_type == MT.CREDSDENIED || msg_type == MT.REGISTERSUCCESS || msg_type == MT.REGISTERFAILED) {
            data = MT.REGISTERFAILED+"$"; //no message
        }

        if (this.game_in_progress == true) { //these messages are only allowed to be send when a game is in progress,
            if (msg_type == MT.UPDATEENTITY || msg_type == MT.KILLENTITY || msg_type == MT.LOADMAP || msg_type == MT.SENDCHAT || msg_type == MT.BINDCAM || msg_type == MT.UPDATEPARTICLE || msg_type == MT.CHOOSECLASS) {
                data = msg_type+"$"+msg;
            }
            /*
            UPDATEENTITY - tell client the position of all entites
            KILLENTITY - tell client to remove client from their render queue; in this case, msg is the entity id
            LOADMAP - msg is the filepath of the map image
            SENDCHAT - msg is the new chat msg
            BINDCAM - msg is an x and y value of where the camera should be at
            UPDATEPARTICLE -
             */
        }

        assert (data != null): "empty message";
        return data;
    }

    private void input_unpacker(String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        MT msg_type = MT.valueOf(msg[0].toUpperCase());

        if (msg_type == MT.CHATMSG) {
            Global.game.new_chat_msg(msg[1]);
        } else if (msg_type == MT.CMD) {
            String[] cmd_msg = msg[1].split(" ");
            execute_command(cmd_msg);
        } else if (msg_type == MT.CHECKCREDS) {
            String[] cred = msg[1].split(",");
            if (Global.db.checkCredentials(cred[0],cred[1])) {

                String json_data = Global.db.getData(cred[0]).replaceAll("\\s",""); //get rid of all white space in json
                send_msg(MT.CREDSACCEPTED,json_data); //if the creds are accepted, send the data to client

            } //if the creds work
            else { send_msg(MT.CREDSDENIED,""); } //if they dont
        } else if (msg_type == MT.STARTGAME) {
            this.enableGIP();
            send_msg(MT.CHOOSECLASS,""); //tell users to choose class when they join the game
            this.teamtag = Global.game.chooseTeam();
            Global.game.new_chat_msg("USER has joined the game!");
        } else if (msg_type == MT.LEAVEGAME) {
            this.disableGIP();
            removePlayerFromGame();
            Global.game.new_chat_msg("USER has left the game!");
        } else if (msg_type == MT.REGISTER) {
            String[] user_data = msg[1].split(",");
            boolean register_success = Global.db.new_user(user_data[0],user_data[1]); //atempt to create a new user

            if (register_success) { this.send_msg(MT.REGISTERSUCCESS,""); }
            else { this.send_msg(MT.REGISTERFAILED,""); }
        } else if (msg_type == MT.RESPAWN) {
            String[] data = msg[1].split(","); //message comes in the form: class,weapon,ability

            init_client_entity(data[0],data[1],data[2]);

        }

        //the following is code that will only work if client_entity is enabled
        if (client_entity == null) { return; }

        if (msg_type == MT.USIN) {
            this.client_entity.handleInput(msg[1]);
        }
    }

    public void init_client_entity(String player_class,String weapon,String ability) {
        this.client_entity = new Player(player_class,weapon,ability,this.teamtag,this);
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

    public void removeClient() { BallClientHandler.client_list.remove(this); }

    public void enableGIP() { this.game_in_progress = true; } //enable game in progress
    public void disableGIP() { this.game_in_progress = false; } //disable game in progress
    public boolean isGameInProgress() { return this.game_in_progress; }
}



/* Shinobi Network
 ______   __       __   ______   __   __   ______
/\  ___\ /\ \     /\ \ /\  ___\ /\ "-.\ \ /\__  _\
\ \ \____\ \ \____\ \ \\ \  __\ \ \ \-.  \\/_/\ \/
 \ \_____\\ \_____\\ \_\\ \_____\\ \_\\"\_\  \ \_\
  \/_____/ \/_____/ \/_/ \/_____/ \/_/ \/_/   \/_/

    The magical connection to the server
 */

package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

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
    private BallClient self; //just a refrence so there can still be access from inside threads
    private boolean game_in_progress = false;

    public BallClient(String ip, int port) {
        this.ip = ip;
        this.port = port;
        this.self = this;
    }

    public boolean start_connection() {
        try { //init stuff
            client_sock = new Socket(this.ip, this.port);
            outstream = new PrintWriter(client_sock.getOutputStream(), true);
            instream = new BufferedReader(new InputStreamReader(client_sock.getInputStream()));
        } catch (IOException ex) {
            System.out.println("Failed to start connection "+ex);
            return false;
        }

        //thread that listens for messages from server
        new Thread(new Runnable() {
            @Override
            public void run() {
                Thread.currentThread().setName("ServerHandler");

                try {
                    while (true) {
                        final String server_msg = instream.readLine();

                       //System.out.println(server_msg);

                        //interperate server message and post to rendering thread
                        Gdx.app.postRunnable(new Runnable() {
                            @Override public void run() { in_unpacker(server_msg); }
                        });

                    }
                } catch(IOException ex) {
                    System.out.println(ex);
                    Global.game.setScreen(Global.game.retryconnection_screen); //move player to attempt reconnection screen if connection is loss
                }

            }
        }).start();
        return true;
    }

    public void close_connection() {
        try {
            outstream.close();
            instream.close();
            client_sock.close();
        } catch(IOException ex) {
            System.out.println(ex);
        }
    }

    //Can be used from anywhere in the main thread to send messages
    public void send_msg(MT msg_type,String msg) { //send message to server
        String raw_msg = this.out_packer(msg_type,msg);
        outstream.println(raw_msg);
    }

    public void send_chat_msg(String msg) { //decide wether or not the message is a chat message or a command
        assert (msg != null && !msg.equals("")): "empty message";
        System.out.println(msg.charAt(0));
        if (msg.charAt(0) == '/') { //if the message begains with a slash, it is a command
            send_msg(MT.CMD,msg.substring(1)); //remove the /
        } else { send_msg(MT.CHATMSG,msg); }
    }

    private String out_packer(MT msg_type,String msg) { //helper method that 'encodes' message
        String data = null;

        //These messages are allowed to be sent outside of game
        if (msg_type == MT.STARTGAME || msg_type == MT.CHECKCREDS || msg_type == MT.REGISTER) {
            data = msg_type+"$"+msg;
        } else if (msg_type == MT.LEAVEGAME) { //special case
            data = msg_type+"$";
            Entity.clearEntityLib();
            HealthTracker.clearBars();
        }
        /*
        CHECKCREDS - message is in the format: username, password
        REGISTER - message is in the format: username, password
         */

        //the following only work if game is in progress
        if (this.game_in_progress == true) {
            if (msg_type == MT.USIN || msg_type == MT.CHATMSG || msg_type == MT.CMD || msg_type == MT.RESPAWN) {
                data = msg_type+"$"+msg;
            }
        }

        assert (data != null): "empty message";
        return data;
    }

    public static void in_unpacker(String raw_msg) {
        //Message packet is in the form MSGTYPE$message
        String[] msg = raw_msg.split("\\$");
        MT msg_type = MT.valueOf(msg[0].toUpperCase());

        if (msg_type == MT.UPDATEENTITY) { //this message contains info about all the entities on the server
            if (msg.length < 2) { return; } //if the message is empty for some reason
            String[] pos = msg[1].split(" "); //break nessage apart into indiviudal entitiyes
            for (String s : pos) {
                Entity.update_entity(s); //update them all
            }

        } else if (msg_type == MT.KILLENTITY) { //if entity is no longer in existance on server, remove from lib
            int id = Integer.parseInt(msg[1]);
            Entity.kill_entity(id);

        } else if (msg_type == MT.SENDCHAT) { //recieve updated chat from server
            String[] chat = msg[1].split(",");
            Global.chatlog.recieve_message(chat[0],chat[1]);

        } else if (msg_type == MT.BINDCAM) { //server tells us where out cam should be positioed
            String[] pos = msg[1].split(",");
            Global.camera.bindPos(new Vector2(Float.parseFloat(pos[0]),Float.parseFloat(pos[1])));

        } else if (msg_type == MT.UPDATEPARTICLE) { //new particle
            String[] particle_list = msg[1].split(" ");
            for (String particle : particle_list) {
                String[] data = particle.split(","); //data comes in the form: name,x,y,duration
                Entity entity = Entity.getEntity(Integer.parseInt(data[0]));
                new Particle(entity,data[1],(int)Float.parseFloat(data[2]));
            }

        } else if (msg_type == MT.CHOOSECLASS) { //server is requesting that the user should pick a class
            Global.game.game_screen.show_death_screen();

        } else if (msg_type == MT.CREDSACCEPTED) { //credential accepted, you may login!!!
            System.out.println("CREDS ACCEPTED");

            Global.user_data = UserData.init_client(msg[1]); //TODO: LINK READ USER DATA
            Global.game.login_screen.creds_accepted();

        } else if (msg_type == MT.CREDSDENIED) { //creds denied, try again
            Global.game.login_screen.creds_declined();

        } else if (msg_type == MT.REGISTERSUCCESS) { //registered successfully, you may now log in
            Global.game.login_screen.register_success();

        } else if (msg_type == MT.REGISTERFAILED) { // regestrarion failed, username taken
            Global.game.login_screen.register_failed();

        } else if (msg_type == MT.UPDATEHP) { //update all the hp bars
            String[] hp_data = msg[1].split(" "); //message comes in the form  id1,hp1 id2,hp2
            HealthTracker.update_data(hp_data);

        } else if (msg_type == MT.GAMEOVER) { //unused.... for now

        } else if (msg_type == MT.UPDATELEADERBOARD) {
            //message comes in the form: name1,kills1,deaths1,damage1 name2,kills2,deaths2,damage2
            String[] entries = msg[1].split(" ");

        } else if (msg_type == MT.PLAYSOUND) { //server is telling us to play a specific sound
            AudioPlayer.play_sound(msg[1]);
        }
    }

    //setters
    public void enableGIP() { this.game_in_progress = true; } //enable game in progress
    public void disableGIP() { this.game_in_progress = false; } //disable game in progress

    //getters
    public boolean isGameInProgress() { return this.game_in_progress; }

}

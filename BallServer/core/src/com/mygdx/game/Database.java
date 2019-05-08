package com.mygdx.game;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.*;
import com.google.firebase.cloud.FirestoreClient;
import com.google.firestore.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Database {

    public static void authSDK() {
        try {
            FileInputStream serviceAccount = new FileInputStream("./service-account.json");
            FirebaseOptions options = new FirebaseOptions.Builder().setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            FirebaseApp.initializeApp(options);
            Firestore database = FirestoreClient.getFirestore();

        } catch (FileNotFoundException ex) {
            System.out.println(ex+" service-account not found.");
        } catch (IOException ex) {
            System.out.println(ex);
        }


    }
}

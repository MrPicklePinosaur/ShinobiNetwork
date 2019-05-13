package com.mygdx.game;

/*
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
*/
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;

import com.google.cloud.firestore.FirestoreOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/*
public class Database {

    public Database() {
        try {
            InputStream serviceAccount = new FileInputStream("service-account.json");
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);

            Firestore db = serviceAccount.getService();

        } catch (FileNotFoundException ex) {
            System.out.println(ex+" service-account not found.");
        } catch (IOException ex) {
            System.out.println(ex);
        }

    }


}
*/

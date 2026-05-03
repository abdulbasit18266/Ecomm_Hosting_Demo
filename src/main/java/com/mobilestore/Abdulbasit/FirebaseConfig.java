package com.mobilestore.Abdulbasit;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() {
        try {
            // 1. Railway ke environment variable se JSON String uthana
            String firebaseConfigJson = System.getenv("FIREBASE_CONFIG_JSON");

            if (firebaseConfigJson == null || firebaseConfigJson.isEmpty()) {
                throw new IOException("Environment Variable 'FIREBASE_CONFIG_JSON' is missing or empty!");
            }

            // 2. String ko InputStream mein convert karna
            InputStream serviceAccount = new ByteArrayInputStream(firebaseConfigJson.getBytes());

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            System.out.println("--------------------------------------------");
            System.out.println("🔥🔥 FIREBASE CONNECTION SUCCESSFUL! 🔥🔥");
            System.out.println("--------------------------------------------");

        } catch (IOException e) {
            System.out.println("❌ FIREBASE ERROR: " + e.getMessage());
        }
    }
}
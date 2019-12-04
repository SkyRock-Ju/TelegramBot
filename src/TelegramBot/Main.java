package TelegramBot;

import Subscriber.Subscriber;
import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class Main {
    private static final String DATABASE_URL = "https://subscribersdatabase.firebaseio.com";

    public static Firestore firestore;

    public static void main(String[] args) {

        initializeFirebase();
        initializeTelegramBotApi();

    }

    public static void initializeFirebase() {
        try {
            FileInputStream refreshToken =
                    new FileInputStream("C:\\WeatherBot\\needs\\subscribersdatabase-firebase-adminsdk-gyx4g-9e32c12fbe.json");

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(refreshToken))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();

            FirebaseApp.initializeApp(options);
            firestore = FirestoreClient.getFirestore();
            getQuoteFromFirestore(firestore);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void initializeTelegramBotApi() {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
            botOptions.setProxyPort(39431);
            botOptions.setProxyHost("66.110.216.105");
            telegramBotsApi.registerBot(new WeatherBot(botOptions));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * This method have to import all subscribers from firestore and put them to WeatherBot.subscribers
     *
     * @param db
     */
    public static void getQuoteFromFirestore(Firestore db){
        if (db.collection("subscribers") != null) {
            db.collection("subscribers").listDocuments().forEach(documentReference -> {
                try {
                    WeatherBot.subscribers.put(documentReference.getId(),(Subscriber)
                            documentReference.get().get().get(documentReference.getId()));
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void getQuoteFromFirestore(Firestore db, String userID) {
        DocumentReference docRef = db.collection("subscribers").document(userID);
        ApiFuture<WriteResult> delete = docRef.delete();
        if (delete.isDone()){
            System.out.println("successfully deleted at:");
        }
    }
}

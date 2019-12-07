package TelegramBot;

import Subscriber.DatabaseEdit;
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

public class Main {
    private static final String DATABASE_URL = "https://subscribersdatabase.firebaseio.com";

    public static Firestore firestore;

    public static void main(String[] args) {

        initializeFirebase();
        initializeTelegramBotApi();
        DatabaseEdit.loadSubscribersFromFirestore(firestore);
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
}

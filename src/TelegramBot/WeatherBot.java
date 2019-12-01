package TelegramBot;

import com.google.cloud.firestore.*;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class WeatherBot extends TelegramLongPollingBot {
    private WeatherCall weatherParser = new WeatherCall();
    private static Map<String, GeoPoint> subscribers = new HashMap<>();

    public WeatherBot(DefaultBotOptions options) {
        super(options);
    }

    public static Map<String, GeoPoint> getSubscribers() {
        return subscribers;
    }

    public void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void sendMsgToSubscriber(Message message, String userId) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (subscribers.containsKey(userId) && subscribers.get(userId) != null) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.enableMarkdown(true);
                    sendMessage.setChatId(message.getChatId().toString());
                    sendMessage.setReplyToMessageId(message.getMessageId());
                    sendMessage.setText(weatherParser.getReadyForecast(
                            String.valueOf(subscribers.get(userId).getLatitude()),//todo
                            String.valueOf(subscribers.get(userId).getLongitude())));//todo
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    timer.cancel();
                }
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(60));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().getLocation() != null) {
            Message message = update.getMessage();
            GeoPoint geoPoint = new GeoPoint(message.getLocation().getLatitude(),message.getLocation().getLongitude());
            String userID = message.getFrom().getId().toString();
            if (subscribers.containsKey(userID) && subscribers.get(userID) == null) {
                subscribers.put(userID, geoPoint);
                sendMsgToSubscriber(message, userID);
            } else {
                sendMsg(message, weatherParser.getReadyForecast(
                        String.valueOf(geoPoint.getLatitude()),
                        String.valueOf(geoPoint.getLongitude())));
            }
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String userID = message.getFrom().getId().toString();
            switch (message.getText().toLowerCase()) {
                case (Commands.HELP):
                    sendMsg(message, "Hello " + message.getFrom().getFirstName() + "! " +
                            "I am weather forecast bot." + "\n" +
                            "Please send your location to get 24 hours weather forecast." + "\n" +
                            "Or if you want to subscribe, please enter in chat: /subscribe");
                    break;
                case (Commands.SUBSCRIBE):
                    if (subscribers.containsKey(userID)) {
                        sendMsg(message, "You already subscribed. Enter in chat \"/help\" for information.");
                        break;
                    } else {
                        subscribers.put(userID, null);
                        sendMsg(message, "You successfully subscribed. Now send your location.");
                        break;
                    }
                case (Commands.UNSUBSCRIBE):
                    if (subscribers.containsKey(userID)) {
                        subscribers.remove(userID);
                        sendMsg(message, "Successfully unsubscribed.");
                        break;
                    }
                    sendMsg(message, "You are not subscriber. Enter in chat \"/help\" for information.");
            }
        }
    }


    @Override
    public String getBotUsername() {
        String botUsername;
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\WeatherBot\\TelegramBot\\src\\TelegramBot\\config"))) {
            botUsername = reader.readLine().split(";")[1];
            return botUsername;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getBotToken() {
        String botToken;
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\WeatherBot\\TelegramBot\\src\\TelegramBot\\config"))) {
            botToken = reader.readLine().split(";")[3];
            return botToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getQuoteFromFirestore () throws ExecutionException, InterruptedException, IOException {
        /*
        DocumentReference docRef = db.collection("subscribers").document("XeeMQa1KeKCKCFoM9PO2");
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();
        if (document.exists()){
            for(HashMap<String,Object> item:document.getData())
        } else {
            System.out.println("No such document!");
        }*/
    }
}



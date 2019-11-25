package TelegramBot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class WeatherBot extends TelegramLongPollingBot {
    private WeatherCall weatherParser = new WeatherCall();
    private Map<Integer, Location> subscribers = new HashMap<>();

    public WeatherBot(DefaultBotOptions options) {
        super(options);
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

    public void sendMsgToSubscriber(Message message, Integer userId) {
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
                            String.valueOf(Math.round(subscribers.get(userId).getLatitude())),
                            String.valueOf(Math.round(subscribers.get(userId).getLongitude()))));
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
        if (update.hasMessage()) {
            Message message = update.getMessage();
            int userId = message.getFrom().getId();
            if (message.getLocation() != null && !subscribers.containsKey(userId)) {
                sendMsg(message, weatherParser.getReadyForecast(
                        String.valueOf(Math.round(message.getLocation().getLatitude())),
                        String.valueOf(Math.round(message.getLocation().getLongitude()))));
            } else if (message.getLocation() != null && subscribers.containsKey(userId)) {
                subscribers.put(userId, message.getLocation());
                sendMsgToSubscriber(message, userId);
            }
            switch (message.getText().toLowerCase()) {
                case (Commands.HELP):
                    sendMsg(message, "Hello " + message.getFrom().getFirstName() + "! " +
                            "I am weather forecast bot." + "\n" +
                            "Please send your location to get 24 hours weather forecast." + "\n" +
                            "Or if you want to subscribe, please enter in chat: /subscribe");
                    break;
                case (Commands.SUBSCRIBE):
                    if (subscribers.containsKey(userId))
                        sendMsg(message, "You already subscribed. Enter in chat \"/help\" for information");
                    subscribers.put(userId, new Location());
                    sendMsg(message, "You successfully subscribed. Now send your location.");
            }
        }
    }


    @Override
    public String getBotUsername() {
        String botUsername;
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\sp\\Desktop\\Ju\\TelegramBot\\TelegramBot\\src\\TelegramBot\\config"))) {
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
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\sp\\Desktop\\Ju\\TelegramBot\\TelegramBot\\src\\TelegramBot\\config"))) {
            botToken = reader.readLine().split(";")[3];
            return botToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}



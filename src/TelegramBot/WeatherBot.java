package TelegramBot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class WeatherBot extends TelegramLongPollingBot {
    private WeatherCall weatherParser = new WeatherCall();
    private ArrayList<Integer> subscribers = new ArrayList<>();

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

    public void sendMsgToSubscriber(Message message, String text, Subscriber subscriber){
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (subscribers.contains(subscriber.getUserId())) {
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
                } else {
                    timer.cancel();
                }
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(25));
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            if (message.getText().equals("/start")) {
                sendMsg(message, "Hello! Enter the city in chat and get 24 hours forecast! " +
                        "For example: \"/New York\" or \"/Istanbul\""+ "\n" +
                        "Or if you want to subscribe to weather forecast, please enter in chat: " +
                        "/subscribe \"Your City\"");
            } else if (message.getText().startsWith("/subscribe ")) {
                if (weatherParser.getReadyForecast(message.getText().substring(11)).startsWith("Can't find")) {
                    sendMsg(message, "Can't find \"%s\" city. Try another one, for example: \"/subscribe Kyiv\" or \"/subscibe Moscow\"");
                } else {
                    sendMsg(message, "Thanks for your subscribe");
                    Subscriber subscriber = new Subscriber(message.getFrom().getId(), message.getText().substring(11));
                    subscribers.add(subscriber.getUserId());
                    sendMsgToSubscriber(message, weatherParser.getReadyForecast(subscriber.getCity()), subscriber);
                }
            } else if (message.getText().equals("/unsubscribe")) {
                if (subscribers.contains(message.getFrom().getId())) {
                    subscribers.remove(message.getFrom().getId());
                    sendMsg(message, "unsubscribe successful");
                } else {
                    sendMsg(message, "You are not subscribed");
                }
            } else {
                sendMsg(message, weatherParser.getReadyForecast(message.getText().substring(1)));
            }
        }
    }


    @Override
    public String getBotUsername() {
        return "julusskyrockbot";
    }

    @Override
    public String getBotToken() {
        return "809926521:AAFKVXlJ7XuyLmmuzwJOxX0jp48hJhxyE_M";
    }
}



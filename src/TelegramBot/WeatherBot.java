package TelegramBot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class WeatherBot extends TelegramLongPollingBot {
    private WeatherCall weatherParser = new WeatherCall();
    private Map<Integer, Subscriber> subscribers = new HashMap<>();

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
                if (subscribers.containsKey(userId)) {
                    SendMessage sendMessage = new SendMessage();
                    sendMessage.enableMarkdown(true);
                    sendMessage.setChatId(message.getChatId().toString());
                    sendMessage.setReplyToMessageId(message.getMessageId());
                    for (String city : subscribers.get(userId).getCity()) {
                        sendMessage.setText(weatherParser.getReadyForecast(city));
                        try {
                            execute(sendMessage);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    timer.cancel();
                }
            }
        }, TimeUnit.SECONDS.toMillis(1), TimeUnit.SECONDS.toMillis(60));
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            int userSubId = message.getFrom().getId();
            if (message.getText().equals(Commands.help)) {
                sendMsg(message, "Hello " + message.getFrom().getFirstName() + "! " + "I am weather forecast bot." + "\n" +
                        "Write the city you want to know weather forecast with sign '/' before." + "\n" +
                        "Example: /Moscow or /Saint Petersburg." + "\n" +
                        "You will get 24h weather forecast." + "\n" + "\n" +
                        "Or if you want to subscribe to weather forecast, please enter in chat: /subscribe \\\"Your City\\\"");
            } else if (message.getText().startsWith(Commands.subscribe) &&
                    !(weatherParser.getReadyForecast(message.getText().substring(11)).startsWith("Can't find"))) {
                String userSubCity = message.getText().substring(11);
                if (subscribers.containsKey(userSubId)) {
                    subscribers.get(userSubId).addCityForSub(userSubCity);
                    sendMsg(message, "You successfully added " + userSubCity + " city.");
                } else {
                    subscribers.put(userSubId, new Subscriber(userSubCity));
                    sendMsg(message, "successfully subscribed to " + userSubCity + " city.");
                    sendMsgToSubscriber(message, userSubId);
                }
            } else if (message.getText().startsWith(Commands.unsubscribe) && subscribers.containsKey(userSubId)) {
                subscribers.remove(userSubId);
                sendMsg(message, "Unsubscribe successful.");
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



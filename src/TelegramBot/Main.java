package TelegramBot;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

    private static String BOT_NAME = "ItsRainyTodayBot";
    private static String BOT_TOKEN = "993545437:AAFeguQgNNRShMM-QeB66U8DI80lnEaiGlY" /* your bot's token here */;

    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {

            telegramBotsApi.registerBot(new WeatherBot(BOT_TOKEN, BOT_NAME));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

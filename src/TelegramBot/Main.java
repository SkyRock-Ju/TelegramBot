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

            DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
            botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
            botOptions.setProxyPort(30212);
            botOptions.setProxyHost("192.169.201.24");

            telegramBotsApi.registerBot(new WeatherBot(botOptions));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

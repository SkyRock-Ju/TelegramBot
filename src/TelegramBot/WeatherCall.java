package TelegramBot;

import WeatherApiJson.WeatherJsonParse;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WeatherCall {

    private final static String API_CALL_TEMPLATE = "https://api.openweathermap.org/data/2.5/forecast?lat=";
    private String apiKey;
    private final static String USER_AGENT = "Mozilla/5.0";
    private final static DateTimeFormatter INPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final static DateTimeFormatter OUTPUT_DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("MMM-dd HH:mm", Locale.US);

    public WeatherCall() {
        String apiKey;
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\WeatherBot\\TelegramBot\\src\\TelegramBot\\config"))) {
            apiKey = reader.readLine().split(";")[5];
            this.apiKey = apiKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getReadyForecast(String latitude, String longitude) {
        String result = "";
        try {
            String jsonRawData = downloadJsonRawData(latitude, longitude);
            List<String> linesOfForecast = convertRawDataToList(jsonRawData);
            for (int i = 0;i<linesOfForecast.size() - 1;i++)
                result += linesOfForecast.get(i) + "\n";
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "The service is not available, please try later";
        }
    }

    private String downloadJsonRawData(String latitude, String longitude) throws Exception {
        String urlString = API_CALL_TEMPLATE + latitude + "&lon=" + longitude + apiKey;
        URL urlObject = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) urlObject.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = connection.getResponseCode();
        if (responseCode == 404)
            throw new IllegalArgumentException();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

    private static List<String> convertRawDataToList(String data) {
        List<String> weatherList = new ArrayList<>();
        WeatherJsonParse weatherJson = new Gson().fromJson(data, WeatherJsonParse.class);
        int lessThen24h = 0;
        for (WeatherApiJson.List time : weatherJson.getList()) {
            if (TimeSet.getTimeSet().contains(time.getDtTxt().substring(11, 16)) && lessThen24h < 10) {
                String formattedForecast = formatForecastData(time.getDtTxt(), time.getMain().getTemp(), time.getWeather().get(0).getDescription());
                weatherList.add(formattedForecast);
                lessThen24h++;
            }
        }
        return weatherList;
    }

    private static String formatForecastData(String data, double temp, String formattedDescription) {
        LocalDateTime forecastDateTime = LocalDateTime.parse(data, INPUT_DATE_TIME_FORMAT);
        String formattedDateTime = forecastDateTime.format(OUTPUT_DATE_TIME_FORMAT);
        String formattedTemperature;
        if (temp > 0) {
            formattedTemperature = "+" + Math.round(temp) + "℃";
        } else {
            formattedTemperature = Math.round(temp) + "℃";
        }
        return String.format("%s   %s %s", formattedDateTime, formattedTemperature, formattedDescription);
    }
}
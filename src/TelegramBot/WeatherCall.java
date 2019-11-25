package TelegramBot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
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

    public WeatherCall (){
        String apiKey;
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\79627\\Desktop\\for me\\myProject\\src\\TelegramBot\\config"))){
            apiKey = reader.readLine().split(";")[5];
            this.apiKey = apiKey;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getReadyForecast(String latitude, String longitude) {
        String result;
        try {
            String jsonRawData = downloadJsonRawData(latitude, longitude);
            List<String> linesOfForecast = convertRawDataToList(jsonRawData);
            result = String.format("%s:%s", System.lineSeparator(), parseForecastDataFromList(linesOfForecast.subList(0, 9)));
        } catch (Exception e) {
            e.printStackTrace();
            return "The service is not available, please try later";
        }
        return result;
    }

    private String downloadJsonRawData(String  latitude, String longitude) throws Exception {
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

    private static List<String> convertRawDataToList(String data) throws Exception {
        List<String> weatherList = new ArrayList<>();

        JsonNode arrNode = new ObjectMapper().readTree(data).get("list");
        if (arrNode.isArray()) {
            for (final JsonNode objNode : arrNode) {
                String forecastTime = objNode.get("dt_txt").toString();
                if (forecastTime.contains(TimeSet.getTimeSet())) {
                    weatherList.add(objNode.toString());
                }
            }
        }
        return weatherList;
    }

    private static String parseForecastDataFromList(List<String> weatherList) throws Exception {
        final StringBuffer sb = new StringBuffer();
        ObjectMapper objectMapper = new ObjectMapper();

        for (String line : weatherList) {
            String dateTime;
            JsonNode mainMode;
            JsonNode weatherArrNode;
            try {
                mainMode = objectMapper.readTree(line).get("main");
                weatherArrNode = objectMapper.readTree(line).get("weather");
                for (final JsonNode objNode : weatherArrNode) {
                    dateTime = objectMapper.readTree(line).get("dt_txt").toString();
                    sb.append(formatForecastData(dateTime, objNode.get("main").toString(), mainMode.get("temp").asDouble()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private static String formatForecastData(String dateTime, String description, double temperature) {
        LocalDateTime forecastDateTime = LocalDateTime.parse(dateTime.replaceAll("\"", ""), INPUT_DATE_TIME_FORMAT);
        String formattedDateTime = forecastDateTime.format(OUTPUT_DATE_TIME_FORMAT);

        String formattedTemperature;
        long roundedTemperature = Math.round(temperature);
        if (roundedTemperature > 0) {
            formattedTemperature = "+" + String.valueOf(Math.round(temperature));
        } else {
            formattedTemperature = String.valueOf(Math.round(temperature));
        }
        String formattedDescription = description.replaceAll("\"", "");

        String weatherIconCode = WeatherUtils.weatherIconsCodes.get(formattedDescription);

        return String.format("%s   %s %s %s%s", formattedDateTime, formattedTemperature, formattedDescription, weatherIconCode, System.lineSeparator());
    }
}
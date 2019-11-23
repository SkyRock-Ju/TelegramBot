package TelegramBot;

import java.util.ArrayList;

public class WeatherJson {
    private String cod;
    private float message;
    private float cnt;

    ArrayList< Object > list = new ArrayList < Object > ();
    private City CityObject;

    public WeatherJson() {
    }


    // Getter Methods

    public ArrayList<Object> getList() {
        return list;
    }

    public String getCod() {
        return cod;
    }

    public float getMessage() {
        return message;
    }

    public float getCnt() {
        return cnt;
    }

    public City getCity() {
        return CityObject;
    }

    // Setter Methods

    public void setCod(String cod) {
        this.cod = cod;
    }

    public void setMessage(float message) {
        this.message = message;
    }

    public void setCnt(float cnt) {
        this.cnt = cnt;
    }

    public void setCity(City cityObject) {
        this.CityObject = cityObject;
    }
}
class City {
    private float id;
    private String name;
    private Coord CoordObject;
    private String country;
    private float population;
    private float timezone;
    private float sunrise;
    private float sunset;


    // Getter Methods

    public float getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coord getCoord() {
        return CoordObject;
    }

    public String getCountry() {
        return country;
    }

    public float getPopulation() {
        return population;
    }

    public float getTimezone() {
        return timezone;
    }

    public float getSunrise() {
        return sunrise;
    }

    public float getSunset() {
        return sunset;
    }

    // Setter Methods

    public void setId(float id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoord(Coord coordObject) {
        this.CoordObject = coordObject;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setPopulation(float population) {
        this.population = population;
    }

    public void setTimezone(float timezone) {
        this.timezone = timezone;
    }

    public void setSunrise(float sunrise) {
        this.sunrise = sunrise;
    }

    public void setSunset(float sunset) {
        this.sunset = sunset;
    }
}
class Coord {
    private float lat;
    private float lon;


    // Getter Methods

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    // Setter Methods

    public void setLat(float lat) {
        this.lat = lat;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }
}
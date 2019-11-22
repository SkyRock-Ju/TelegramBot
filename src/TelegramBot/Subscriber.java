package TelegramBot;
import java.util.ArrayList;

public class Subscriber {
    private ArrayList<String> city = new ArrayList<>();

    public Subscriber(String city) {
        this.city.add(city);
    }

    public void addCityForSub (String city){
        this.city.add(city);
    }

    public ArrayList<String> getCity (){
        return city;
    }
}
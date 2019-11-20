package TelegramBot;

public class Subscriber {

    private int userId;
    private String city;

    public Subscriber(int userId, String city) {
        this.userId = userId;
        this.city = city;
    }

    public String getCity() {
        return city;
    }

    public int getUserId() {
        return userId;
    }
}

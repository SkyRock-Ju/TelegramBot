package TelegramBot;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TimeSet {
    private static String[] timeArray = {"09:00", "12:00", "15:00", "18:00", "21:00", "00:00", "03:00", "06:00"};
    private static Set<String> timeSet = new HashSet<>(Arrays.asList(timeArray));

    public static Set<String> getTimeSet() {
        return timeSet;
    }
}

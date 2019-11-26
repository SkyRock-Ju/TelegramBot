package TelegramBot;

import java.util.*;

public class TimeSet {
    private static Set<String>
            timeSet = new HashSet<>(Arrays.asList("09:00", "12:00", "15:00", "18:00", "21:00", "00:00", "03:00", "06:00"));

    public static Set<String> getTimeSet() {
        return timeSet;
    }
}

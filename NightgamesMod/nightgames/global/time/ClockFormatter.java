package nightgames.global.time;

/**
 * Formats a Clock time as a user-friendly string.
 */
public class ClockFormatter {
    private static String amPm(int hour) {
        return hour < 12 ? "am" : "pm";
    }

    private static int zeroHour(int hour) {
        return hour == 0 ? 12 : hour;
    }

    public static String clockString(int hour, int minute) {
        assert hour < 24;
        if (hour == 0 && minute == 0) {
            return "midnight";
        } else if (hour == 12) {
            return "noon";
        } else {
            return String.format("%d:%2d %s", zeroHour(hour), minute, amPm(hour));
        }

    }
}

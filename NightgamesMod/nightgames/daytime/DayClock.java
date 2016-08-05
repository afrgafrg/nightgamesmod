package nightgames.daytime;

import nightgames.global.time.Clock;

/**
 * TODO: Write class-level documentation.
 */
public class DayClock implements Clock {
    private int time;   // One time increment is one hour.
    private int startTime;
    private static final int END_TIME = 22;
    private boolean started = false;

    void startDay(int startTime) {
        this.startTime = startTime;
        time = startTime;
        started = true;
    }

    int duration() {
        return END_TIME - startTime;
    }

    boolean started() {
        return started;
    }

    boolean dayOver() {
        return !(time < END_TIME);
    }

    String endTimeString() {
        return clockString(END_TIME);
    }

    boolean enoughFor(Activity activity) {
        return time + activity.duration() < END_TIME;
    }

    private static int getHour(int someTime) {
        return someTime;
    }

    @Override public int getHour() {
        return getHour(time);
    }


    private static String clockString(int someTime) {
        int hour = getHour(someTime);
        if (hour < 12) {
            return hour + ":00 am";
        } else if (hour == 12) {
            return "noon";
        } else {
            return hour % 12 + ":00 pm";
        }
    }

    @Override public String clockString() {
        return clockString(time);
    }

    @Override public void tick() {
        time = (time + 1) % 24;
    }
}

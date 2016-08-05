package nightgames.daytime;

import nightgames.global.time.Clock;
import nightgames.global.time.ClockFormatter;

/**
 * Clock that tracks time during the day in increments of one hour.
 */
class DayClock implements Clock {
    private int time;
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

    static String endTimeString() {
        return ClockFormatter.clockString(END_TIME, 0);
    }

    boolean enoughTimeFor(Activity activity) {
        return time + activity.duration() < END_TIME;
    }

    private static int getHour(int someTime) {
        return someTime;
    }

    @Override public int getHour() {
        return getHour(time);
    }

    @Override public int getMinute() {
        return 0;
    }

    @Override public void tick() {
        time = (time + 1) % 24;
    }
}

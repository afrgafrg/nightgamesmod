package nightgames.global.time;

/**
 * A Clock tracks time. Different Clocks may use different time increments.
 */
public interface Clock {
    int getHour();

    int getMinute();

    default String clockString() {
        return ClockFormatter.clockString(getHour(), getMinute());
    }

    void tick();

    default void tick(int ticks) {
        for (int i = 0; i < ticks; i++) {
            tick();
        }
    }

}

package nightgames.global.time;

/**
 * A Clock tracks time and provides a user-friendly string representation of the current time. Different Clocks may
 * track different time increments.
 */
public interface Clock {
    int getHour();

    String clockString();

    void tick();

    default void tick(int ticks) {
        for (int i = 0; i < ticks; i++) {
            tick();
        }
    }
}

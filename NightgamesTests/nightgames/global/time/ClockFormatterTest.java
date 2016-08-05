package nightgames.global.time;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

/**
 * Tests for the ClockFormatter.
 */
public class ClockFormatterTest {
    @Test public void midnight() throws Exception {
        assertThat(ClockFormatter.clockString(0, 0), equalTo("midnight"));
    }

    @Test public void afterMidnight() throws Exception {
        assertThat(ClockFormatter.clockString(0, 30), equalTo("12:30 am"));
    }

    @Test public void am() throws Exception {
        assertThat(ClockFormatter.clockString(10, 0), equalTo("10:00 am"));
    }

    @Test public void noon() throws Exception {
        assertThat(ClockFormatter.clockString(12, 0), equalTo("noon"));
    }

    @Test public void pm() throws Exception {
        assertThat(ClockFormatter.clockString(18, 30), equalTo("6:30 pm"));
    }
}

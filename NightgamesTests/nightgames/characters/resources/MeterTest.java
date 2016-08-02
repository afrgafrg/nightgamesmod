package nightgames.characters.resources;

import org.junit.Before;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertThat;

/**
 * Tests for the Meter class.
 */
public class MeterTest {
    private Meter meter;
    private TestMeterObserver observer;

    @Before public void setUp() throws Exception {
        meter = new Meter(Resource.AROUSAL, 100);
        observer = new TestMeterObserver();
        meter.addObserver(observer);
    }

    @Test public void get() throws Exception {
        meter.set(50);
        assertThat(meter.get(), equalTo(50));
        meter.set(101);
        assertThat(meter.get(), equalTo(100));
        meter.set(-1);
        assertThat(meter.get(), equalTo(0));
    }

    @Test public void getReal() throws Exception {

        meter.set(50);
        assertThat(meter.getReal(), equalTo(50));
        meter.set(101);
        assertThat(meter.getReal(), equalTo(101));
        meter.set(-1);
        assertThat(meter.getReal(), equalTo(0));
    }

    @Test public void set() throws Exception {
        meter.set(150);
        assertThat(observer.latestValue, equalTo(150));
        meter.set(0);
        assertThat(observer.latestValue, equalTo(0));
        meter.set(-1);
        assertThat(observer.latestValue, equalTo(0));
    }

    @Test public void getOverflow() throws Exception {
        meter.set(150);
        assertThat(meter.getOverflow(), equalTo(50));
        meter.set(50);
        assertThat(meter.getOverflow(), equalTo(0));
    }

    @Test public void max() throws Exception {
        assertThat(meter.max(), equalTo(100));
        meter.setTemporaryMax(50);
        assertThat(meter.max(), equalTo(50));
        meter.setTemporaryMax(150);
        assertThat(meter.max(), equalTo(100));
    }

    @Test public void setMax() throws Exception {
        meter.setMax(50);
        assertThat((int) observer.latestMax, equalTo(50));
        meter.setMax(-1);
        assertThat((int) observer.latestMax, equalTo(0));
        meter.setMax(150);
        assertThat((int) observer.latestMax, equalTo(150));
    }

    @Test public void setTemporaryMax() throws Exception {
        meter.setTemporaryMax(50);
        assertThat(observer.latestTempMax, equalTo(50));
        assertThat((int) observer.latestMax, equalTo(100));
        meter.setTemporaryMax(-1);
        assertThat(observer.latestTempMax, equalTo(Integer.MAX_VALUE));
        assertThat((int) observer.latestMax, equalTo(100));
        meter.setTemporaryMax(150);
        assertThat(observer.latestTempMax, equalTo(150));
        assertThat((int) observer.latestMax, equalTo(100));
    }

    @Test public void reduce() throws Exception {
        meter.set(100);
        meter.reduce(50);
        assertThat(observer.latestValue, equalTo(50));
        meter.reduce(51);
        assertThat(observer.latestValue, equalTo(0));
    }

    @Test public void restore() throws Exception {
        meter.set(0);
        meter.restore(50);
        assertThat(observer.latestValue, equalTo(50));
        meter.restore(51);
        assertThat(observer.latestValue, equalTo(100));
    }

    @Test public void restoreNoLimit() throws Exception {
        meter.set(0);
        meter.restoreNoLimit(50);
        assertThat(observer.latestValue, equalTo(50));
        meter.restoreNoLimit(51);
        assertThat(observer.latestValue, equalTo(101));
    }

    @Test public void isEmpty() throws Exception {
        meter.set(100);
        assertThat(meter.isEmpty(), is(false));
        meter.set(50);
        assertThat(meter.isEmpty(), is(false));
        meter.set(0);
        assertThat(meter.isEmpty(), is(true));
    }

    @Test public void isFull() throws Exception {
        meter.set(100);
        assertThat(meter.isFull(), is(true));
        meter.set(50);
        assertThat(meter.isFull(), is(false));
        meter.set(0);
        assertThat(meter.isFull(), is(false));
    }

    @Test public void empty() throws Exception {
        meter.empty();
        assertThat(meter.getReal(), equalTo(0));
    }

    @Test public void fill() throws Exception {
        meter.fill();
        assertThat(meter.getReal(), equalTo(100));
    }

    @Test public void gain() throws Exception {
        meter.set(50);
        meter.setTemporaryMax(75);
        meter.gain(50);
        assertThat(observer.latestValue, equalTo(50));
        assertThat((int) observer.latestMax, equalTo(150));
        assertThat(observer.latestTempMax, greaterThan((int) observer.latestMax));
        meter.setMax(100);
        meter.set(200);
        meter.gain(50);
        assertThat(observer.latestValue, equalTo(150));
        assertThat((int) observer.latestMax, equalTo(150));
        assertThat(observer.latestTempMax, greaterThan((int) observer.latestMax));
    }

    @Test public void percent() throws Exception {
        meter.setMax(200);
        meter.set(50);
        assertThat(meter.percent(), equalTo(25));
        meter.set(201);
        assertThat(meter.percent(), equalTo(100));
    }

    private class TestMeterObserver implements Observer {
        int latestValue;
        float latestMax;
        int latestTempMax;

        @Override public void update(Observable o, Object arg) {
            Meter meter = (Meter) o;
            latestValue = meter.getReal();
            latestMax = meter.trueMax();
            latestTempMax = meter.getTemporaryMax();
        }
    }

}

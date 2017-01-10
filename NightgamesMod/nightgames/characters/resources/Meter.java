package nightgames.characters.resources;

import nightgames.global.Formatter;
import nightgames.global.Global;

import java.io.Serializable;
import java.util.Observable;

/**
 * Container for holding and interacting with one of a character's Resources.
 */
public class Meter extends Observable implements Serializable, Cloneable {
    private static final long serialVersionUID = 2L;

    public final Resource resource;
    private int current;
    private float max;
    private int temporaryMax;

    public Meter(Resource resource, int max) {
        this.resource = resource;
        this.max = max;
        this.temporaryMax = Integer.MAX_VALUE;
        this.current = 0;
    }

    private void setAll(int newCurrent, float newMax, int newTemporaryMax) {
        this.current = Math.max(0, newCurrent);
        this.max = Math.max(0, newMax);
        this.temporaryMax = newTemporaryMax > 0 ? newTemporaryMax : Integer.MAX_VALUE;
        setChanged();
        notifyObservers();
    }

    public int get() {
        return Math.min(current, max());
    }

    public int getReal() {
        return current;
    }

    public void set(int i) {
        setAll(i, max, temporaryMax);
    }

    public int getOverflow() {
        return Math.max(0, current - max());
    }

    public int max() {
        return (int) maxFull();
    }

    public float maxFull() {
        return Math.min(max, temporaryMax);
    }

    public int intTrueMax() {
        return (int) trueMax();
    }

    public float trueMax() {
        return max;
    }

    public void setMax(float i) {
        setAll(current, Math.max(i, 0), temporaryMax);
    }

    int getTemporaryMax() {
        return temporaryMax;
    }

    public void setTemporaryMax(int i) {
        setAll(current, max, i);
    }

    public void reduce(int i) {
        set(current - i);
    }

    public void restore(int i) {
        set(Math.min(max(), current + i));
    }

    public void restoreNoLimit(int i) {
        set(current += i);
    }

    public boolean isEmpty() {
        return current <= 0;
    }

    public boolean isFull() {
        return current >= max();
    }

    public void empty() {
        set(0);
    }

    public void fill() {
        set(Math.max(max(), current));
    }

    public void gain(float i) {
        float newMax = max + i;
        int newCurrent = (int) Math.min(current, newMax);
        // Remove limiting temporaryMax
        setAll(newCurrent, newMax, -1);
    }

    public int percent() {
        return Math.min(100, 100 * current / max());
    }

    @Override public Meter clone() throws CloneNotSupportedException {
        return (Meter) super.clone();
    }

    @Override public String toString() {
        return String.format("current: %s / max: %s", Formatter.formatDecimal(current),
                        Formatter.formatDecimal(max()));
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Meter meter = (Meter) o;

        return resource == meter.resource;

    }

    @Override public int hashCode() {
        return resource.hashCode();
    }
}

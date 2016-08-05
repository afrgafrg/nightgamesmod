package nightgames.global.time;

import nightgames.global.Grammar;

/**
 * Tracks number of days since the game started.
 */
public class NgsDate {
    private int date;

    public NgsDate(int date) {
        this.date = date;
    }

    public NgsDate() {
        this(1);
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void advance() {
        date++;
    }

    public boolean isWeekend() {
        int day = date % 7;
        return day == 6 || day == 0;
    }

    public String dateString(Time time) {
        return String.format("%s %d", Grammar.capitalizeFirstLetter(time.desc), date);
    }

    @Override public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        NgsDate ngsDate = (NgsDate) o;

        return date == ngsDate.date;

    }

    @Override public int hashCode() {
        return date;
    }
}

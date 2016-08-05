package nightgames.match;

import nightgames.global.time.Clock;

class MatchTime implements Clock {
    private int time; // number of 5-minute turns since the match started
    static final int MATCH_START_TIME = 22; // Matches start at 10 PM
    static final int MATCH_END_TIME = 1; // Matches end at 1 AM

    MatchTime() {
        this(0);
    }

    private MatchTime(int time) {
        this.time = time;
    }

    public int time() {
        return time;
    }

    @Override public int getHour() {
        return MATCH_START_TIME + time / 12;    // there are 12 5-minute increments in
    }

    @Override public String clockString() {
        int hour = getHour();
        if (hour > 12) {
            hour = hour % 12;
        }
        if (time % 12 < 2) {
            return hour + ":0" + time % 12 * 5;
        } else {
            return hour + ":" + time % 12 * 5;
        }
    }

    @Override public void tick() {
        time = (time + 1);
    }
}

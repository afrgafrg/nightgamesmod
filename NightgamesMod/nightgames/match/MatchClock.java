package nightgames.match;

import nightgames.global.time.Clock;


/**
 * Clock that tracks number of 5-minute turns since the match started.
 */
class MatchClock implements Clock {
    private int turns; // One turn is 5 minutes long
    private static final int MATCH_START_TIME = 22; // Matches start at 10 PM
    private static final int MATCH_DURATION = 36; // Matches last for 3 hours

    MatchClock() {
        this(0);
    }

    private MatchClock(int turns) {
        this.turns = turns;
    }

    public int turns() {
        return turns;
    }

    public boolean matchOver() {
        return turns >= MATCH_DURATION;
    }

    @Override public int getHour() {
        return (MATCH_START_TIME + turns / 12) % 24;    // there are 12 5-minute increments in an hour
    }

    @Override public int getMinute() {
        return (turns * 5) % 60;
    }

    @Override public void tick() {
        turns = (turns + 1);
    }
}

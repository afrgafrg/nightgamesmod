package nightgames.global;

import java.util.List;
import java.util.Optional;

public class Random {
    private static java.util.Random rng = new java.util.Random();
    private static java.util.Random FROZEN_RNG = new java.util.Random();

    public static int random(int start, int end) {
        return rng.nextInt(end - start) + start;
    }

    public static int random(int d) {
        if (d <= 0) {
            return 0;
        }
        return rng.nextInt(d);
    }// finds a centered random number from [0, d] (inclusive)

    public static int centeredrandom(int d, double center, double sigma) {
        int val = 0;
        center = Math.max(0, Math.min(d, center));
        for (int i = 0; i < 10; i++) {
            double f = rng.nextGaussian() * sigma + center;
            val = (int) Math.round(f);
            if (val >= 0 && val <= d) {
                return val;
            }
        }
        return Math.max(0, Math.min(d, val));
    }

    @SafeVarargs public static <T> Optional<T> pickRandom(T... arr) {
        if (arr == null || arr.length == 0)
            return Optional.empty();
        return Optional.of(arr[Random.random(arr.length)]);
    }

    public static <T> Optional<T> pickRandom(List<T> list) {
        if (list == null || list.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(random(list.size())));
        }
    }

    public static double randomdouble() {
        return rng.nextDouble();
    }

    public static double randomdouble(double to) {
        return randomdouble() * to;
    }

    public static long randomlong() {
        return rng.nextLong();
    }

    /**
     * TODO Huge hack to freeze status descriptions.
     */
    public static void freezeRNG() {
        FROZEN_RNG = rng;
        rng = new java.util.Random(0);
    }

    /**
     * TODO Huge hack to freeze status descriptions.
     */
    public static void unfreezeRNG() {
        FROZEN_RNG = new java.util.Random();
        rng = FROZEN_RNG;
    }

    public static class DieRoll {
        public final int sides;
        public final int roll;
        public final int modifier;

        public DieRoll(int sides, int modifier) {
            this.sides = sides;
            this.modifier = modifier;
            this.roll = Random.random(sides);
        }

        public DieRoll(int modifier) {
            this(20, modifier);
        }

        public int result() {
            return roll + modifier;
        }

        public boolean criticalHit() {
            return roll == 0;
        }

        public boolean criticalMiss() {
            return roll == sides - 1;
        }
    }
}

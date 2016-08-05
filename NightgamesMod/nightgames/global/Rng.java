package nightgames.global;

import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Game functions involving randomness.
 */
public class Rng {
    public static Rng rng = new Rng();

    private Random random;

    public Rng() {
        random = new Random();
    }

    public int random(int d) {
        if (d <= 0) {
            return 0;
        }
        return random.nextInt(d);
    }

    public int random(int start, int end) {
        return random.nextInt(end - start) + start;
    }

    public <T> T pickRandom(T[] arr) {
        if (arr.length == 0) return null;
        return arr[random(arr.length)];
    }

    public <T> Optional<T> pickRandom(List<T> list) {
        if (list.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(random(list.size())));
        }
    }

    public double randomdouble() {
        return random.nextDouble();
    }

    // finds a centered random number from [0, d] (inclusive)
    public int centeredrandom(int d, double center, double sigma) {
        int val = 0;
        center = Math.max(0, Math.min(d, center));
        for (int i = 0; i < 10; i++) {
            double f = random.nextGaussian() * sigma + center;
            val = (int) Math.round(f);
            if (val >= 0 && val <= d) {
                return val;
            }
        }
        return Math.max(0, Math.min(d, val));
    }

    public float randomfloat() {
        return (float) random.nextDouble();
    }

    public String maybeString(String string) {
        if (random(2) == 0) {
            return string;
        } else {
            return "";
        }
    }

    public long randomlong() {
        return random.nextLong();
    }
}

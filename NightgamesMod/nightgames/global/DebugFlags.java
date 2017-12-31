package nightgames.global;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum DebugFlags {
    // TODO: get rid of the DEBUG_ prefix for internal use. Keep it for command-line arguments though.
    DEBUG_SCENE,
    DEBUG_SKILLS,
    DEBUG_STRATEGIES,
    DEBUG_PET,
    DEBUG_SKILLS_RATING,
    DEBUG_MOOD,
    DEBUG_IMAGES,
    DEBUG_DAMAGE,
    DEBUG_PLANNING,
    DEBUG_SKILL_CHOICES,
    DEBUG_LOADING,
    DEBUG_CLOTHING,
    DEBUG_FTC,
    DEBUG_GUI,
    DEBUG_ADDICTION,
    DEBUG_SPECTATE,
    DEBUG_TURN_OFF_GUI;
    public static boolean[] debug = new boolean[values().length];
    public static int debugSimulation = 0;

    public static boolean isDebugOn(DebugFlags flag) {
        return debug[flag.ordinal()] && debugSimulation == 0;
    }

    public static void parseDebugFlags(String... args) throws UnknownDebugFlagException {
        parseDebugFlags(Arrays.asList(args));
    }

    public static void parseDebugFlags(List<String> args) throws UnknownDebugFlagException {
        List<String> invalid = new ArrayList<>();
        for (String arg : args) {
            try {
                DebugFlags flag = valueOf(arg);
                debug[flag.ordinal()] = true;
            } catch (IllegalArgumentException e) {
                invalid.add(arg);
            }
        }
        if (invalid.size() > 0) {
            throw new UnknownDebugFlagException(invalid);
        }
    }

    public static class UnknownDebugFlagException extends Exception {
        private static final long serialVersionUID = -7182162719982400541L;

        UnknownDebugFlagException(List<String> invalidArgs) {
            super("Unknown debug flags: " + String.join(", ", invalidArgs));
        }
    }

}

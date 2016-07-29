package nightgames.global;

/**
 * TODO: Write class-level documentation.
 */
public class Grammar {
    public static String capitalizeFirstLetter(String original) {
        if (original == null) {
            return "";
        }
        if (original.length() == 0) {
            return original;
        }
        return original.substring(0, 1).toUpperCase() + original.substring(1);
    }
}

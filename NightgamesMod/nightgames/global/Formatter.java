package nightgames.global;

import java.text.DecimalFormat;

public class Formatter {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.##");

    public static String formatDecimal(double val) {
        return DECIMAL_FORMAT.format(val);
    }
}

package common.util.parse;

import java.text.DecimalFormat;

/**
 * Created by luke on 10/19/15.
 */
public class TimeUtils {

    public static String formatToSeconds(double ticks) {
        DecimalFormat df = new DecimalFormat("0.0");
        double ticksPerSecond = 20;
        double d = (ticks / ticksPerSecond);

        return df.format(d);
    }

    public static String formatToTimeFromMinutes(int i) {
        if (i == 1) {
            return i + " minute";
        } else if (i < 60) {
            return i + " minutes";
        } else if (i == 60) {
            return "1 hour";
        } else if (i % 60 == 0) {
            return (i / 60) + " hours";
        } else if (i > 60) {
            int hours = i / 60;
            int mins = i % 60;

            return hours + " hours and " + mins + " mins";
        } else {
            return "";
        }
    }
}

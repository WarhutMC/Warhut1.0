package common.util.math;

import java.util.Random;

/**
 * Created by luke on 10/20/15.
 */
public class MathUtils {

    public static double max(double d1, double d2) {
        if (d1 > d2) {
            return d1;
        } else {
            return d2;
        }
    }

    public static double min(double d1, double d2) {
        if (d1 < d2) {
            return d1;
        } else {
            return d2;
        }
    }

    public static int randInt(int min, int max) {
        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }
}

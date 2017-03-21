package common.util;

import common.kit.Kit;

import java.util.Comparator;

/**
 * Created by luke on 11/15/15.
 */
public class Comp implements Comparator<Kit> {
    public int compare(Kit o1, Kit o2) {
        if (o1.price > o2.price) {
            return 1;
        } else if (o1.price < o2.price) {
            return -1;
        } else {
            return 0;
        }
    }
}

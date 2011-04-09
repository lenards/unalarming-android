package com.sixfifty;

import java.util.Calendar;

/**
 * Helper methods for dealing with time calculations. 
 * 
 * @author lenards, BalusC
 *
 */
public class TimeHelper {
    /**
     * Computes the elapsed time between two time-periods. 
     * 
     * @author BalusC - http://stackoverflow.com/users/157882/balusc
     * @see http://stackoverflow.com/questions/567659/calculate-elapsed-time-in-java-groovy
     * 
     * @param before the time prior to after
     * @param after the time following before
     * @param field the field of the Calendar (YEAR, DATE, MINUTE, HOUR_OF_DAY)
     * @return integer representing the time difference for the field
     */
    public static int elapsed(Calendar before, Calendar after, int field) {
        Calendar clone = (Calendar) before.clone(); // Otherwise changes are been reflected.
        int elapsed = -1;
        while (!clone.after(after)) {
            clone.add(field, 1);
            elapsed++;
        }
        return elapsed;
    }
}

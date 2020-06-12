/*
 *  SleepTimeTest.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import org.junit.Test;

import java.util.Calendar;

import static org.junit.Assert.*;

public class SleepTimeCalculatorTest {

    @Test
    public void calculateBefore12() {
        //  Current time: 08:12(:30, but seconds are ignored)
        //  Time until 12PM: 03:48
        //  Difference in minutes: (3 * 60) + 48 = 228

        //  Given
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 12);
        cal.set(Calendar.SECOND, 30);

        //  When
        SleepTimeCalculator st = new SleepTimeCalculator(cal.getTime());

        //  Then
        assertEquals(228, st.calculate());
    }

    @Test
    public void calculateAfter12() {
        //  Current time: 15:15(:15, but seconds are ignored)
        //  Time until 12PM: 20:45
        //  Difference in minutes: (20 * 60) + 45

        //  Given
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 15);

        //  When
        SleepTimeCalculator st = new SleepTimeCalculator(cal.getTime());

        //  Then
        assertEquals(1245, st.calculate());
    }
}
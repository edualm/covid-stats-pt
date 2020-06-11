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

public class SleepTimeTest {

    @Test
    public void calculateBefore12() {
        //  Given
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 12);
        cal.set(Calendar.SECOND, 30);

        //  When
        SleepTime st = new SleepTime(cal.getTime());

        //  Then
        assertEquals(((12 - 9) * 60) + (60 - 12), st.calculate());
    }

    @Test
    public void calculateAfter12() {
        //  Given
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 15);
        cal.set(Calendar.MINUTE, 15);
        cal.set(Calendar.SECOND, 15);

        //  When
        SleepTime st = new SleepTime(cal.getTime());

        //  Then
        assertEquals(((24 - (15 - 12) - 1) * 60) + (60 - 15), st.calculate());
    }
}
/*
 *  SleepTime.java
 *  covid-stats-pt
 *
 *  Created by Eduardo Almeida <hello at edr dot io>
 *  Published under the public domain
 */

package io.edr.covidstatspt;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class SleepTimeCalculator {

    private final Date date;

    SleepTimeCalculator() {
        this.date = new Date();
    }

    SleepTimeCalculator(Date date) {
        this.date = date;
    }

    int calculate() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(date);

        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);

        if (hourOfDay < 12) {
            int minutes = calendar.get(Calendar.MINUTE);

            return (12 - hourOfDay - 1) * 60 + (60 - minutes);
        }

        int minutes = calendar.get(Calendar.MINUTE);

        return (12 + (24 - hourOfDay) - 1) * 60 + (60 - minutes);
    }

}

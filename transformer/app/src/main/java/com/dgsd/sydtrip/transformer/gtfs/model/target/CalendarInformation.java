package com.dgsd.sydtrip.transformer.gtfs.model.target;

import com.dgsd.sydtrip.transformer.gtfs.model.staging.GtfsStagingCalendar;
import com.dgsd.sydtrip.transformer.gtfs.model.staging.GtfsStagingCalendarDate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarInformation {

    private final int startJulianDate;

    private final int endJulianDate;

    private final int availabilityBitmask;

    private final Map<Integer, Integer> julianDayToExceptionMap;

    public CalendarInformation(GtfsStagingCalendar calendar, List<GtfsStagingCalendarDate> dates) {
        startJulianDate = calendar.getStartJulianDay();
        endJulianDate = calendar.getEndJulianDay();
        availabilityBitmask = calendar.getDayBitmask();

        julianDayToExceptionMap = new HashMap<>();
        if (dates != null) {
            dates.forEach(date -> {
                julianDayToExceptionMap.put(date.getJulianDay(), date.getExceptionType());
            });
        }
    }

    public int getStartJulianDate() {
        return startJulianDate;
    }

    public int getEndJulianDate() {
        return endJulianDate;
    }

    public int getAvailabilityBitmask() {
        return availabilityBitmask;
    }

    public Map<Integer, Integer> getJulianDayToExceptionMap() {
        return julianDayToExceptionMap;
    }

    @Override
    public String toString() {
        return "CalendarInformation{" +
                "startJulianDate=" + startJulianDate +
                ", endJulianDate=" + endJulianDate +
                ", availabilityBitmask=" + availabilityBitmask +
                ", julianDayToExceptionMap=" + julianDayToExceptionMap +
                '}';
    }
}
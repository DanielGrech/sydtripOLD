package com.dgsd.sydtrip.transformer.gtfs.model.target;

import com.dgsd.sydtrip.transformer.gtfs.model.staging.GtfsStagingTrip;

import java.util.List;

public class Trip {

    private final int id;

    private final Route route;

    private final List<StopTime> stops;

    private final CalendarInformation calendarInformation;

    private final String headSign;

    private final int direction;

    private final String blockId;

    private final boolean wheelchairAccessible;

    public Trip(GtfsStagingTrip trip, List<StopTime> stops,
                Route route, CalendarInformation calendarInformation) {
        this.id = trip.getId();
        this.headSign = trip.getHeadSign();
        this.direction = trip.getDirection();
        this.blockId = trip.getBlockId();
        this.wheelchairAccessible = trip.isWheelchairAccessible();

        this.stops = stops;
        this.route = route;
        this.calendarInformation = calendarInformation;
    }

    public int getId() {
        return id;
    }

    public Route getRoute() {
        return route;
    }

    public List<StopTime> getStops() {
        return stops;
    }

    public CalendarInformation getCalendarInformation() {
        return calendarInformation;
    }

    public String getHeadSign() {
        return headSign;
    }

    public int getDirection() {
        return direction;
    }

    public String getBlockId() {
        return blockId;
    }

    public boolean isWheelchairAccessible() {
        return wheelchairAccessible;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", route=" + route +
                ", stops=" + stops +
                ", calendarInformation=" + calendarInformation +
                ", headSign='" + headSign + '\'' +
                ", direction=" + direction +
                ", blockId='" + blockId + '\'' +
                ", wheelchairAccessible=" + wheelchairAccessible +
                '}';
    }
}

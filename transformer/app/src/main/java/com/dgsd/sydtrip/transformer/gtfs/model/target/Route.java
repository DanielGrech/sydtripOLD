package com.dgsd.sydtrip.transformer.gtfs.model.target;

import com.dgsd.sydtrip.transformer.gtfs.model.staging.GtfsStagingRoute;

public class Route {

    private final int id;

    private final int agencyId;

    private final String shortName;

    private final String longName;

    private final int routeType;

    private final int color;

    public Route(GtfsStagingRoute route) {
        id = route.getId();
        agencyId = route.getAgencyId();
        shortName = route.getShortName();
        longName = route.getLongName();
        routeType = route.getRouteType();
        color = route.getColor();
    }

    public int getId() {
        return id;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public String getShortName() {
        return shortName;
    }

    public String getLongName() {
        return longName;
    }

    public int getRouteType() {
        return routeType;
    }

    public int getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", agencyId=" + agencyId +
                ", shortName='" + shortName + '\'' +
                ", longName='" + longName + '\'' +
                ", routeType=" + routeType +
                ", color=" + color +
                '}';
    }
}

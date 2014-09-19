package com.dgsd.sydtrip.transformer.gtfs.model.target;

import com.dgsd.sydtrip.transformer.gtfs.model.staging.GtfsStagingAgency;

public class Agency {

    private final int id;

    private final String name;

    public Agency(GtfsStagingAgency agency) {
        this.id = agency.getId();
        this.name = agency.getName();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "GtfsStagingAgency{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}

package com.sampleapplication.bookit.model;

import java.io.Serializable;

public class RouteInfo implements Serializable {

    public String arrival;
    public String departure;
    public long total;
    public long totalwaitlist;

    public String getArrival() {
        return arrival;
    }

    public void setArrival(String arrival) {
        this.arrival = arrival;
    }

    public String getDeparture() {
        return departure;
    }

    public void setDeparture(String departure) {
        this.departure = departure;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getTotalwaitlist() {
        return totalwaitlist;
    }

    public void setTotalwaitlist(long totalwaitlist) {
        this.totalwaitlist = totalwaitlist;
    }
}

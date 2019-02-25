package com.sampleapplication.bookit.model;

import java.io.Serializable;

public class SeatAvailable implements Serializable {

    public String id;
    public String name;
    public String code;
    public long total;

    public SeatCount seatCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public SeatCount getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(SeatCount seatCount) {
        this.seatCount = seatCount;
    }
}

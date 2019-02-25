package com.sampleapplication.bookit.model;

import java.io.Serializable;
import java.util.List;

public class Train implements Serializable {

    public String trainCode;
    public String name;
    public List<Integer> routes;
    public List<String> days;
    public String time;

    public String getTrainCode() {
        return trainCode;
    }

    public void setTrainCode(String trainCode) {
        this.trainCode = trainCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getRoutes() {
        return routes;
    }

    public void setRoutes(List<Integer> routes) {
        this.routes = routes;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

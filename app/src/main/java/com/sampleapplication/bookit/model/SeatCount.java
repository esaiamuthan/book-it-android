package com.sampleapplication.bookit.model;

import java.io.Serializable;

public class SeatCount implements Serializable {

    public String documentId;
    public String trainId;
    public String bookedDate;
    public long bookedTickets;

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTrainId() {
        return trainId;
    }

    public void setTrainId(String trainId) {
        this.trainId = trainId;
    }

    public String getBookedDate() {
        return bookedDate;
    }

    public void setBookedDate(String bookedDate) {
        this.bookedDate = bookedDate;
    }

    public long getBookedTickets() {
        return bookedTickets;
    }

    public void setBookedTickets(long bookedTickets) {
        this.bookedTickets = bookedTickets;
    }
}

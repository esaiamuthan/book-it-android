package com.sampleapplication.bookit.history;

import com.sampleapplication.bookit.model.BookingInfo;

public interface OnBookingClickListener {

    public void onBookingClick(BookingInfo bookingInfo);

    public void onPassengerInfo(BookingInfo bookingInfo);
}

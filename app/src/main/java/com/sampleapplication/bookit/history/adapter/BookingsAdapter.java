package com.sampleapplication.bookit.history.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bookit.app.R;
import com.sampleapplication.bookit.history.OnBookingClickListener;
import com.sampleapplication.bookit.model.BookingInfo;

import java.util.ArrayList;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {

    ArrayList<BookingInfo> bookingList = new ArrayList<>();
    private OnBookingClickListener onBookingClickListener;

    public BookingsAdapter(ArrayList<BookingInfo> bookingList,
                           OnBookingClickListener onBookingClickListener) {
        this.bookingList = bookingList;
        this.onBookingClickListener = onBookingClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_booking_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(bookingList.get(position));

        holder.itemView.setOnClickListener(view -> {
            onBookingClickListener.onBookingClick(bookingList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public void notifyData(ArrayList<BookingInfo> bookingList) {
        this.bookingList = bookingList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDate, tvTrainInfo, tvJourneyInfo;
        ImageButton ivInfoButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvTrainInfo = itemView.findViewById(R.id.tvTrainInfo);
            tvJourneyInfo = itemView.findViewById(R.id.tvJourneyInfo);
            ivInfoButton = itemView.findViewById(R.id.ivInfoButton);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(BookingInfo bookingInfo) {
            tvDate.setText("Date : " + bookingInfo.getDate() + " Time : " + bookingInfo.getTrainTime());
            tvTrainInfo.setText(bookingInfo.getTrainName() + " - " + bookingInfo.getTrain());
            tvJourneyInfo.setText("From : " + bookingInfo.getSource() + " To : " + bookingInfo.getDestination());

            ivInfoButton.setOnClickListener(v -> {
                onBookingClickListener.onPassengerInfo(bookingInfo);
            });
        }
    }
}

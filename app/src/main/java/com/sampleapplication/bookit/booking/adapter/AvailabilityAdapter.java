package com.sampleapplication.bookit.booking.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bookit.app.R;
import com.sampleapplication.bookit.booking.OnBookSelectListener;
import com.sampleapplication.bookit.model.SeatAvailable;
import com.sampleapplication.bookit.model.Station;

import java.util.ArrayList;

public class AvailabilityAdapter extends RecyclerView.Adapter<AvailabilityAdapter.ViewHolder> {

    ArrayList<SeatAvailable> availabilityList = new ArrayList<>();
    OnBookSelectListener onBookSelectListener;
    Station sourceStation;

    public AvailabilityAdapter(ArrayList<SeatAvailable> availabilityList,
                               OnBookSelectListener onBookSelectListener, Station sourceStation) {
        this.availabilityList = availabilityList;
        this.onBookSelectListener = onBookSelectListener;
        this.sourceStation = sourceStation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_availablity_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(availabilityList.get(position));
        holder.tvBookNow.setOnClickListener(v -> {
            onBookSelectListener.onBookSelect(availabilityList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return availabilityList.size();
    }

    public void notifyData(ArrayList<SeatAvailable> availabilityList) {
        this.availabilityList = availabilityList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvFromTo;
        TextView tvAvailability;
        TextView tvBookNow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFromTo = itemView.findViewById(R.id.tvFromTo);
            tvAvailability = itemView.findViewById(R.id.tvAvailability);
            tvBookNow = itemView.findViewById(R.id.tvBookNow);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(SeatAvailable seatAvailable) {
            tvFromTo.setText(
                    "From : " + sourceStation.getName() + " To : " + seatAvailable.getName()
            );

            if (seatAvailable.getSeatCount() != null) {
                if (seatAvailable.getSeatCount().getBookedTickets()
                        < seatAvailable.getTotal()) {

                    tvAvailability.setText(
                            (seatAvailable.getTotal() - seatAvailable.getSeatCount().getBookedTickets()) + " seat(s) available"
                    );
                    tvBookNow.setVisibility(View.VISIBLE);
                } else {
                    tvAvailability.setText(
                            "No seat(s) available"
                    );
                    tvBookNow.setVisibility(View.GONE);
                }
            } else {
                tvAvailability.setText(
                        seatAvailable.getTotal() + " seat(s) available"
                );
                tvBookNow.setVisibility(View.VISIBLE);
            }
        }
    }
}

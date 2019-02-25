package com.sampleapplication.bookit.booking.adapter;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bookit.app.R;
import com.sampleapplication.bookit.booking.OnStationSelectListener;
import com.sampleapplication.bookit.history.OnBookingClickListener;
import com.sampleapplication.bookit.model.BookingInfo;
import com.sampleapplication.bookit.model.Station;

import java.util.ArrayList;

public class StationsAdapter extends RecyclerView.Adapter<StationsAdapter.ViewHolder> {

    ArrayList<Station> stationsList = new ArrayList<>();
    private OnStationSelectListener onStationSelectListener;

    public StationsAdapter(ArrayList<Station> stationsList,
                           OnStationSelectListener onStationSelectListener) {
        this.stationsList = stationsList;
        this.onStationSelectListener = onStationSelectListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_station_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(stationsList.get(position));

        holder.itemView.setOnClickListener(view -> {
            onStationSelectListener.onStationSelect(stationsList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return stationsList.size();
    }

    public void notifyData(ArrayList<Station> stationsList) {
        this.stationsList = stationsList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvStationName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStationName = itemView.findViewById(R.id.tvStationName);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(Station station) {
            tvStationName.setText(station.getName() + " (" + station.getCode() + ")");
        }
    }
}

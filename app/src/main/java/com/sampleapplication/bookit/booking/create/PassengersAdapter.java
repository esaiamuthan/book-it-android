package com.sampleapplication.bookit.booking.create;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bookit.app.R;
import com.sampleapplication.bookit.model.Passenger;

import java.util.ArrayList;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.ViewHolder> {

    ArrayList<Passenger> passengersList = new ArrayList<>();

    public PassengersAdapter(ArrayList<Passenger> passengersList) {
        this.passengersList = passengersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_passengers_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(passengersList.get(position));
    }

    @Override
    public int getItemCount() {
        return passengersList.size();
    }

    public void notifyData(ArrayList<Passenger> passengersList) {
        this.passengersList = passengersList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvBerth;
        TextView tvAgeWithGender;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBerth = itemView.findViewById(R.id.tvBerth);
            tvAgeWithGender = itemView.findViewById(R.id.tvAgeWithGender);
            tvName = itemView.findViewById(R.id.tvName);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(Passenger passenger) {
            tvBerth.setText("Berth : " + passenger.getBerth());
            if (passenger.getpGender() == 1)
                tvAgeWithGender.setText("Age : " + passenger.getBerth() + ", Gender : Male");
            else if (passenger.getpGender() == 2)
                tvAgeWithGender.setText("Age : " + passenger.getBerth() + ", Gender : Female");
            else if (passenger.getpGender() == 3)
                tvAgeWithGender.setText("Age : " + passenger.getBerth() + ", Gender : Other");
            tvName.setText(getAdapterPosition() + 1 + ". Name : " + passenger.getpName());
        }
    }
}

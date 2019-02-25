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
import com.sampleapplication.bookit.booking.OnTrainSelectListener;
import com.sampleapplication.bookit.model.Station;
import com.sampleapplication.bookit.model.Train;

import java.util.ArrayList;

public class TrainsAdapter extends RecyclerView.Adapter<TrainsAdapter.ViewHolder> {

    ArrayList<Train> trainsList = new ArrayList<>();
    private OnTrainSelectListener onTrainSelectListener;

    Station sourceStation;
    Station destStation;

    public TrainsAdapter(ArrayList<Train> trainsList,
                         OnTrainSelectListener onTrainSelectListener, Station sourceStation, Station destStation) {
        this.trainsList = trainsList;
        this.onTrainSelectListener = onTrainSelectListener;
        this.sourceStation = sourceStation;
        this.destStation = destStation;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_trains_list, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bindView(trainsList.get(position));

        holder.itemView.setOnClickListener(view -> {
            onTrainSelectListener.onTrainSelect(trainsList.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return trainsList.size();
    }

    public void notifyData(ArrayList<Train> trainsList, Station sourceStation, Station destStation) {
        if (sourceStation != null)
            this.sourceStation = sourceStation;
        if (destStation != null)
            this.destStation = destStation;
        this.trainsList = trainsList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTrainName;
        TextView tvStationCode;
        TextView tvDays;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTrainName = itemView.findViewById(R.id.tvTrainName);
            tvStationCode = itemView.findViewById(R.id.tvStationCode);
            tvDays = itemView.findViewById(R.id.tvDays);
        }

        @SuppressLint("SetTextI18n")
        public void bindView(Train train) {
            tvTrainName.setText(train.getName() + " (" + train.getTrainCode() + ")");
            tvStationCode.setText(sourceStation.getCode() + " -> " + destStation.getCode());

            String days = "";

            for (String day : train.getDays()) {
                days += day + " ";
            }
            tvDays.setText(days);
        }
    }
}

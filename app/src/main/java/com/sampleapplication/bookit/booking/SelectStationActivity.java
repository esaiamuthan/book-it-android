package com.sampleapplication.bookit.booking;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bookit.app.R;
import com.bookit.app.databinding.ActivitySearchBinding;
import com.bookit.app.databinding.ActivitySelectStationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.booking.adapter.StationsAdapter;
import com.sampleapplication.bookit.model.Station;
import com.sampleapplication.bookit.model.User;

import java.util.ArrayList;
import java.util.Objects;

public class SelectStationActivity extends BaseActivity
        implements OnStationSelectListener {

    private ActivitySelectStationBinding binding;

    private String TAG = SelectStationActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ArrayList<Station> stationsList = new ArrayList<>();

    StationsAdapter stationsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_select_station);
        initUI();

    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.contentSelectStation.rvStations.setLayoutManager(new LinearLayoutManager(this));
        stationsAdapter = new StationsAdapter(stationsList, this);
        binding.contentSelectStation.rvStations.setAdapter(stationsAdapter);

        getStations();
    }

    private void getStations() {
        db.collection("stations")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        stationsList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Station station = document.toObject(Station.class);
                            station.setStationId(Long.parseLong(document.getId()));
                            stationsList.add(station);
                        }
                        stationsAdapter.notifyData(stationsList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        stationsAdapter.notifyData(stationsList);
                    }
                });
    }

    @Override
    public void onStationSelect(Station station) {
        if (getIntent().getExtras().getString("source").equals(station.getName())) {
            Toast.makeText(this, "Source and destination should not be same", Toast.LENGTH_SHORT).show();
        } else {
            Intent intent = new Intent();
            intent.putExtra("station", station);
            intent.putExtra("destination", getIntent().getExtras().getBoolean("destination"));
            setResult(1001, intent);
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}

package com.sampleapplication.bookit.booking;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.bookit.app.R;
import com.bookit.app.databinding.ActivityBookingSearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.history.BookingHistoryActivity;
import com.sampleapplication.bookit.model.Station;
import com.sampleapplication.bookit.model.User;

import java.util.Objects;

public class BookingSearchActivity extends BaseActivity {

    private ActivityBookingSearchBinding binding;

    private String TAG = BookingSearchActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    Station sourceStation;
    Station destStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_search);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sourceStation = (Station) getIntent().getSerializableExtra("sourceStation");
        destStation = (Station) getIntent().getSerializableExtra("destStation");

        binding.contentBookingSearch.tvSourceLocation.setText(
                sourceStation.getName() + " (" + sourceStation.getCode() + ")"
        );
        binding.contentBookingSearch.tvDestinationLocation.setText(
                destStation.getName() + " (" + destStation.getCode() + ")"
        );

        getTrainInfo();
    }

    private void getTrainInfo() {

    }
}

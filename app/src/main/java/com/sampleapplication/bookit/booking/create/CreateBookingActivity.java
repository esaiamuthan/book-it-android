package com.sampleapplication.bookit.booking.create;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.bookit.app.R;
import com.bookit.app.databinding.ActivityCreateBookingBinding;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.booking.SearchActivity;
import com.sampleapplication.bookit.model.Passenger;

import java.util.ArrayList;

public class CreateBookingActivity extends BaseActivity
        implements AddpassengerFragment.OnAddPassengerListener {

    private ActivityCreateBookingBinding binding;
    private ArrayList<Passenger> passengers = new ArrayList<>();

    PassengersAdapter passengersAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_booking);
        initUI();
    }


    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.contentCreateBooking.tvAddPassenger.setOnClickListener(v -> {
            openAddPassengerDialog();
        });

        binding.contentCreateBooking.rvPassengers.setLayoutManager(new LinearLayoutManager(this));
        passengersAdapter = new PassengersAdapter(passengers);
        binding.contentCreateBooking.rvPassengers.setAdapter(passengersAdapter);
    }

    private void openAddPassengerDialog() {
        AddpassengerFragment addpassengerFragment = AddpassengerFragment.newInstance();
        addpassengerFragment.setListener(this);
        addpassengerFragment.show(getSupportFragmentManager(), "AddpassengerFragment");
    }

    @Override
    public void onPassengerAdd(Passenger passenger) {
        if (passengers.size() <6) {
            passengers.add(passenger);
            passengersAdapter.notifyData(passengers);
        }else{
            Toast.makeText(this, "You can add only five passengers", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_submit:
                break;
        }
        return true;
    }
}

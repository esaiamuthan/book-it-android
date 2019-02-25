package com.sampleapplication.bookit.booking.create;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bookit.app.R;
import com.bookit.app.databinding.ActivityCreateBookingBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.booking.SearchActivity;
import com.sampleapplication.bookit.connection.LoginActivity;
import com.sampleapplication.bookit.home.HomeActivity;
import com.sampleapplication.bookit.model.Passenger;
import com.sampleapplication.bookit.model.SeatAvailable;
import com.sampleapplication.bookit.model.Station;
import com.sampleapplication.bookit.model.Train;
import com.sampleapplication.bookit.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateBookingActivity extends BaseActivity
        implements AddpassengerFragment.OnAddPassengerListener {

    private ActivityCreateBookingBinding binding;
    private ArrayList<Passenger> passengers = new ArrayList<>();

    PassengersAdapter passengersAdapter;

    private String TAG = SearchActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    Station sourceStation;
    Station destStation;

    Train selectedTrain;

    String selectedDate;
    String routeId;

    SeatAvailable seatAvailable;

    long currentAvailableSeats = 0;

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

        routeId = getIntent().getExtras().getString("routeId");
        selectedDate = getIntent().getExtras().getString("selectedDate");
        sourceStation = (Station) getIntent().getSerializableExtra("sourceStation");
        destStation = (Station) getIntent().getSerializableExtra("destStation");
        selectedTrain = (Train) getIntent().getSerializableExtra("train");
        seatAvailable = (SeatAvailable) getIntent().getSerializableExtra("seatAvailable");

        if (seatAvailable.getSeatCount() != null) {
            if (seatAvailable.getSeatCount().getBookedTickets()
                    < seatAvailable.getTotal()) {
                currentAvailableSeats = (seatAvailable.getTotal() - seatAvailable.getSeatCount().getBookedTickets());
            } else {
                currentAvailableSeats = 0;
            }
        } else {
            currentAvailableSeats = seatAvailable.getTotal();
        }


        mAuth = FirebaseAuth.getInstance();

        binding.contentCreateBooking.tvAddPassenger.setOnClickListener(v -> {
            if (currentAvailableSeats != 0)
                openAddPassengerDialog();
            else
                Toast.makeText(this, "You can't able to add passenger", Toast.LENGTH_SHORT).show();
        });

        binding.contentCreateBooking.tvSourceLocation.setText(sourceStation.getName() + " (" + sourceStation.getCode() + ")");
        binding.contentCreateBooking.tvDestinationLocation.setText(destStation.getName() + " (" + destStation.getCode() + ")");

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
        passengers.add(passenger);
        if (passengers.size() <= currentAvailableSeats) {
            passengersAdapter.notifyData(passengers);
        } else {
            passengers.remove(passenger);
            Toast.makeText(this, "You can able to add only " + currentAvailableSeats + " passenger(s)", Toast.LENGTH_SHORT).show();
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
                if (passengers.size() > 0)
                    bookTicket();
                else
                    Toast.makeText(this, "Please add minimum one passenger", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    DocumentReference reference;

    private void bookTicket() {
        binding.linearProgress.setVisibility(View.VISIBLE);

        // Create a new user with a first and last name
        Map<String, Object> booking = new HashMap<>();
        booking.put("booked", passengers.size());
        booking.put("destination", destStation.getName());
        booking.put("route", Integer.parseInt(routeId));
        booking.put("status", 1);
        booking.put("source", sourceStation.getName());
        booking.put("train", selectedTrain.getTrainCode());
        booking.put("trainName", selectedTrain.getName());
        booking.put("trainTime", selectedTrain.getTime());
        booking.put("date", selectedDate);
        booking.put("uId", mAuth.getUid());

        db.collection("bookings")
                .add(booking)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        reference = task.getResult();

                        updateBookingCount();

                        addPassengers(0);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void updateBookingCount() {
        Map<String, Object> booking = new HashMap<>();
        booking.put("bookedDate", selectedDate);
        booking.put("bookedTickets", passengers.size());
        booking.put("trainId", selectedTrain.getTrainCode());

        db.collection("available_routes")
                .document(String.valueOf(sourceStation.getStationId()))
                .collection("current_routes")
                .document(String.valueOf(destStation.getStationId()))
                .collection("bookings")
                .add(booking)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Success documents: ");
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });

    }

    private void addPassengers(int position) {
        final int[] currentPosition = {position};
        Passenger passenger = passengers.get(currentPosition[0]);

        Map<String, Object> booking = new HashMap<>();
        booking.put("berth", passenger.getBerth());
        booking.put("pAge", passenger.getpAge());
        booking.put("pBerthPosition", passenger.getpBerthPosition());
        booking.put("pGender", passenger.getpGender());
        booking.put("pName", passenger.getpName());
        booking.put("pProof", passenger.getpProof());
        booking.put("pProofId", passenger.getpProofId());
        booking.put("pProofPosition", passenger.getpProofPosition());

        reference.collection("passengers")
                .add(booking)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        currentPosition[0] = currentPosition[0] + 1;
                        if (currentPosition[0] <= (passengers.size() - 1)) {
                            addPassengers(currentPosition[0]);
                        } else {
                            binding.linearProgress.setVisibility(View.GONE);
                            Toast.makeText(this, "Booked successfully", Toast.LENGTH_SHORT).show();

                            Intent loginValidateIntent = new Intent(this, HomeActivity.class);
                            loginValidateIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(loginValidateIntent);
                            finish();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }
}

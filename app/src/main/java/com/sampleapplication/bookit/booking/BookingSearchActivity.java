package com.sampleapplication.bookit.booking;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.bookit.app.R;
import com.bookit.app.databinding.ActivityBookingSearchBinding;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.booking.create.CreateBookingActivity;
import com.sampleapplication.bookit.model.SeatAvailable;
import com.sampleapplication.bookit.model.SeatCount;
import com.sampleapplication.bookit.model.Station;
import com.sampleapplication.bookit.model.Train;

import java.util.ArrayList;

public class BookingSearchActivity extends BaseActivity
        implements AvailableFragment.OnBookingListener {

    private ActivityBookingSearchBinding binding;

    private String TAG = BookingSearchActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Station sourceStation;
    public Station destStation;

    Train selectedTrain;

    String selectedDate;
    String routeId;

    ArrayList<SeatAvailable> seatAvailableArrayList = new ArrayList<>();

    SeatAvailable currentSearchSeatAvailable;

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

        routeId = getIntent().getExtras().getString("routeId");
        selectedDate = getIntent().getExtras().getString("selectedDate");
        sourceStation = (Station) getIntent().getSerializableExtra("sourceStation");
        destStation = (Station) getIntent().getSerializableExtra("destStation");
        selectedTrain = (Train) getIntent().getSerializableExtra("train");

        binding.contentBookingSearch.tvSourceLocation.setText(
                sourceStation.getName() + " (" + sourceStation.getCode() + ")"
        );
        binding.contentBookingSearch.tvDestinationLocation.setText(
                destStation.getName() + " (" + destStation.getCode() + ")"
        );

        collectionReference = db.collection("available_routes")
                .document(String.valueOf(sourceStation.getStationId()))
                .collection("current_routes");

        getCurrentRouteInfo();

        getRouteInfo();

        binding.contentBookingSearch.tvBook.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateBookingActivity.class);
            intent.putExtra("train", selectedTrain);
            intent.putExtra("routeId", routeId);
            intent.putExtra("sourceStation", sourceStation);
            intent.putExtra("destStation", destStation);
            intent.putExtra("selectedDate", selectedDate);
            intent.putExtra("seatAvailable", currentSearchSeatAvailable);
            startActivity(intent);
        });
    }

    CollectionReference collectionReference;

    private void getCurrentRouteInfo() {
        collectionReference
                .document(String.valueOf(destStation.getStationId()))
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentSearchSeatAvailable = documentSnapshot.toObject(SeatAvailable.class);
                    collectionReference
                            .document(String.valueOf(destStation.getStationId()))
                            .collection("bookings")
                            .whereEqualTo("bookedDate", selectedDate)
                            .whereEqualTo("trainId", selectedTrain.getTrainCode())
                            .limit(1)
                            .get()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    SeatCount seatCount = null;
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        seatCount = document.toObject(SeatCount.class);
                                        seatCount.setDocumentId(document.getId());
                                        Log.d("Train " + TAG, document.getId() + " => " + document.getData());
                                    }
                                    if (seatCount != null)
                                        currentSearchSeatAvailable.setSeatCount(seatCount);
                                    updateCurrentStatus();
                                } else {
                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                    updateCurrentStatus();
                                }
                            });
                });
    }

    private void updateCurrentStatus() {
        if (currentSearchSeatAvailable.getSeatCount() != null) {
            if (currentSearchSeatAvailable.getSeatCount().getBookedTickets()
                    < currentSearchSeatAvailable.getTotal()) {
                binding.contentBookingSearch.tvSeats.setText(
                        (currentSearchSeatAvailable.getTotal() - currentSearchSeatAvailable.getSeatCount().getBookedTickets()) + " seat(s) available"
                );
                binding.contentBookingSearch.tvBook.setVisibility(View.VISIBLE);
            } else {
                binding.contentBookingSearch.tvSeats.setText(
                        "No seats available"
                );
                binding.contentBookingSearch.tvBook.setVisibility(View.GONE);
            }
        } else {
            binding.contentBookingSearch.tvSeats.setText(
                    currentSearchSeatAvailable.getTotal() + " seat(s) available"
            );
            binding.contentBookingSearch.tvBook.setVisibility(View.VISIBLE);
        }
    }

    private void getRouteInfo() {
        binding.linearProgress.setVisibility(View.VISIBLE);
        db.collection("available_routes")
                .document(String.valueOf(sourceStation.getStationId()))
                .collection("current_routes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        seatAvailableArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            SeatAvailable seatAvailable = document.toObject(SeatAvailable.class);
                            seatAvailable.setId(document.getId());

                            if (!destStation.getName().equals(seatAvailable.getName()))
                                seatAvailableArrayList.add(seatAvailable);

                            Log.d("Train " + TAG, document.getId() + " => " + document.getData());
                        }
                        getSeatAvailableForTrainByDate(0);
                    } else {
                        binding.linearProgress.setVisibility(View.GONE);
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private void getSeatAvailableForTrainByDate(int position) {
        final int[] currentPosition = {position};

        SeatAvailable seatAvailable = seatAvailableArrayList.get(currentPosition[0]);

        collectionReference
                .document(String.valueOf(seatAvailable.getId()))
                .collection("bookings")
                .whereEqualTo("bookedDate", selectedDate)
                .whereEqualTo("trainId", selectedTrain.getTrainCode())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        SeatCount seatCount = null;
                        int currentBookedTickets = 0;
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            seatCount = document.toObject(SeatCount.class);
                            seatCount.setDocumentId(document.getId());
                            Log.d("Train " + TAG, document.getId() + " => " + document.getData());

                            currentBookedTickets += seatCount.bookedTickets;
                            seatCount.setBookedTickets(currentBookedTickets);
                        }
                        seatAvailable.setSeatCount(seatCount);
                        seatAvailableArrayList.set(currentPosition[0], seatAvailable);

                        currentPosition[0] = currentPosition[0] + 1;
                        if (currentPosition[0] <= (seatAvailableArrayList.size() - 1)) {
                            getSeatAvailableForTrainByDate(currentPosition[0]);
                        } else {
                            separateList();
                            binding.linearProgress.setVisibility(View.GONE);
                        }
                    } else {
                        binding.linearProgress.setVisibility(View.GONE);
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    ArrayList<SeatAvailable> availableList = new ArrayList<>();
    ArrayList<SeatAvailable> unAvailableList = new ArrayList<>();

    private void separateList() {
        availableList.clear();
        unAvailableList.clear();
        for (SeatAvailable seatAvailable : seatAvailableArrayList) {
            if (seatAvailable.getSeatCount() != null) {
                if (seatAvailable.getSeatCount().getBookedTickets()
                        < seatAvailable.getTotal()) {
                    availableList.add(seatAvailable);
                } else {
                    unAvailableList.add(seatAvailable);
                }
            } else {
                availableList.add(seatAvailable);
            }
        }

        binding.contentBookingSearch.viewPager.setAdapter(new ViewPagerAdapter(
                getSupportFragmentManager(), availableList, unAvailableList
        ));
        binding.contentBookingSearch.tabLayout.setupWithViewPager(
                binding.contentBookingSearch.viewPager
        );

        binding.contentBookingSearch.viewPager.setVisibility(View.VISIBLE);
        binding.contentBookingSearch.tabLayout.setVisibility(View.VISIBLE);
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

    @Override
    public void onBookClicked(SeatAvailable seatAvailable) {
        if (!destStation.getName().equals(seatAvailable.getName())) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(this, R.style.MyDialogTheme);
            builder.setTitle(getString(R.string.app_name));
            builder.setMessage("Your destination has been changed " +
                    "\n Please confirm to change you destination");
            builder.setPositiveButton(getString(android.R.string.ok), (dialog, which) -> {
                dialog.dismiss();

                db.collection("routes")
                        .whereEqualTo("departure", sourceStation.getName())
                        .whereEqualTo("arrival", seatAvailable.getName())
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String newRouteId = null;
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    newRouteId = document.getId();
                                    break;
                                }
                                if (newRouteId != null && newRouteId.length() > 0) {

                                    Station newDest = new Station();
                                    newDest.setName(seatAvailable.getName());
                                    newDest.setCode(seatAvailable.getCode());
                                    newDest.setStationId(Long.parseLong(seatAvailable.getId()));

                                    Intent intent = new Intent(this, CreateBookingActivity.class);
                                    intent.putExtra("train", selectedTrain);
                                    intent.putExtra("routeId", newRouteId);
                                    intent.putExtra("sourceStation", sourceStation);
                                    intent.putExtra("destStation", newDest);
                                    intent.putExtra("selectedDate", selectedDate);
                                    intent.putExtra("seatAvailable", seatAvailable);

                                    startActivity(intent);
                                }
                            } else {
                                Log.d(TAG, "Error getting documents: ", task.getException());
                            }
                        });


            });
            builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                dialog.dismiss();
            });
            builder.create().show();
        }
    }
}

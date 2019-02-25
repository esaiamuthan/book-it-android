package com.sampleapplication.bookit.booking;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.bookit.app.R;
import com.bookit.app.databinding.ActivitySearchBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.booking.adapter.TrainsAdapter;
import com.sampleapplication.bookit.booking.create.CreateBookingActivity;
import com.sampleapplication.bookit.model.RouteInfo;
import com.sampleapplication.bookit.model.Station;
import com.sampleapplication.bookit.model.Train;
import com.sampleapplication.bookit.model.User;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class SearchActivity extends BaseActivity
        implements DatePickerDialog.OnDateSetListener,
        OnTrainSelectListener {


    private ActivitySearchBinding binding;

    private String TAG = SearchActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    private User currentUserInfo;

    Station sourceStation;
    Station destStation;

    Train selectedTrain;

    ArrayList<Train> trainArrayList = new ArrayList<>();

    TrainsAdapter trainsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_search);
        initUI();
    }

    private void moveToSearchResult() {
        db.collection("routes")
                .whereEqualTo("departure", sourceStation.getName())
                .whereEqualTo("arrival", destStation.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String routeId = "";
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            routeId = document.getId();
                            break;
                        }
                        if (routeId.length() > 0) {
                            getTrains(routeId);
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
//        Intent intent = new Intent(this, BookingSearchActivity.class);
//        intent.putExtra("sourceStation", sourceStation);
//        intent.putExtra("destStation", destStation);
//        startActivity(intent);
    }

    private void getTrains(String routeId) {
        binding.linearProgress.setVisibility(View.VISIBLE);
        db.collection("trains")
                .whereArrayContains("routes", Integer.parseInt(routeId))
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        trainArrayList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d("Train " + TAG, document.getId() + " => " + document.getData());
                            Train train = document.toObject(Train.class);
                            train.setTrainCode(document.getId());
                            trainArrayList.add(train);
                        }
                        handleData();
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                    binding.linearProgress.setVisibility(View.GONE);
                });
    }

    private void handleData() {
        trainsAdapter.notifyData(trainArrayList, sourceStation, destStation);

        if (trainArrayList.size() > 0)
            binding.contentSearch.tvAvailable.setVisibility(View.VISIBLE);
        else
            binding.contentSearch.tvAvailable.setVisibility(View.GONE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == 1001) {
                if (data.getExtras().getBoolean("destination")) {
                    destStation = (Station) data.getSerializableExtra("station");
                    binding.contentSearch.etDestination.setText(
                            destStation.getName() + " (" + destStation.getCode() + ")"
                    );
                } else {
                    sourceStation = (Station) data.getSerializableExtra("station");
                    binding.contentSearch.etSource.setText(
                            sourceStation.getName() + " (" + sourceStation.getCode() + ")"
                    );
                }
                trainArrayList.clear();
                handleData();
            }
        }
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        if (currentUserInfo == null) {
            db.collection("bookit")
                    .document(Objects.requireNonNull(mAuth.getUid()))
                    .get().addOnSuccessListener(documentSnapshot -> {
                currentUserInfo = documentSnapshot.toObject(User.class);
            });
        }

        binding.contentSearch.etDate.setOnClickListener(v -> {
            openDatePickerDialog();
        });

        binding.contentSearch.etSource.setOnClickListener(v -> {
            Intent intent = new Intent(this, SelectStationActivity.class);
            if (sourceStation != null)
                intent.putExtra("source", sourceStation.getName());
            else
                intent.putExtra("source", "");
            intent.putExtra("destination", false);
            startActivityForResult(intent, 1000);
        });

        binding.contentSearch.etDestination.setOnClickListener(v -> {
            if (binding.contentSearch.etSource.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please select start location", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(this, SelectStationActivity.class);
                if (sourceStation != null)
                    intent.putExtra("source", sourceStation.getName());
                else
                    intent.putExtra("source", "");
                intent.putExtra("destination", true);
                startActivityForResult(intent, 1000);
            }
        });

        binding.contentSearch.rvTrains.setLayoutManager(new LinearLayoutManager(this));
        trainsAdapter = new TrainsAdapter(trainArrayList, this,
                sourceStation, destStation);
        binding.contentSearch.rvTrains.setAdapter(trainsAdapter);


        Calendar calendar = Calendar.getInstance();
        setDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void openDatePickerDialog() {
        hideKeyboard();
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(this,
                R.style.MyDialogTheme,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        dialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        setDate(year, month, dayOfMonth);
    }

    private void setDate(int year, int month, int dayOfMonth) {
        String dateFormat = "";
        if (month < 10) {
            if (dayOfMonth < 10)
                dateFormat = String.format("0%d-0%d-%d", dayOfMonth, month + 1, year);
            else
                dateFormat = String.format("%d-0%d-%d", dayOfMonth, month + 1, year);
        } else
            dateFormat = String.format("%d-%d-%d", dayOfMonth, month + 1, year);

        binding.contentSearch.etDate.setText(dateFormat);

        trainArrayList.clear();
        handleData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.action_submit:
                if (binding.contentSearch.etSource.getText().toString().isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Please select start location", Toast.LENGTH_SHORT).show();
                } else if (binding.contentSearch.etDestination.getText().toString().isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Please select destination location", Toast.LENGTH_SHORT).show();
                } else if (binding.contentSearch.etDate.getText().toString().isEmpty()) {
                    Toast.makeText(SearchActivity.this, "Please select date", Toast.LENGTH_SHORT).show();
                } else {
                    moveToSearchResult();
                }
                break;
        }
        return true;
    }

    @Override
    public void onTrainSelect(Train train) {
        Intent intent = new Intent(this, CreateBookingActivity.class);
        startActivity(intent);
    }
}

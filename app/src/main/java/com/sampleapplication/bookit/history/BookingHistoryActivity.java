package com.sampleapplication.bookit.history;

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

import com.bookit.app.R;
import com.bookit.app.databinding.ActivityBookingHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.history.adapter.BookingsAdapter;
import com.sampleapplication.bookit.model.BookingInfo;
import com.sampleapplication.bookit.model.Passenger;
import com.sampleapplication.bookit.model.User;

import java.util.ArrayList;
import java.util.Objects;

public class BookingHistoryActivity extends BaseActivity
        implements OnBookingClickListener {

    private ActivityBookingHistoryBinding binding;

    private String TAG = BookingHistoryActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    private User currentUserInfo;

    ArrayList<BookingInfo> bookingList = new ArrayList<>();

    BookingsAdapter bookingsAdapter;

    ArrayList<String> trainList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_booking_history);
        initUI();

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
                getBookingHistory();
            });
        } else
            getBookingHistory();

        binding.contentBookingHistory.rvBookings.setLayoutManager(new LinearLayoutManager(this));
        bookingsAdapter = new BookingsAdapter(bookingList, this);
        binding.contentBookingHistory.rvBookings.setAdapter(bookingsAdapter);
    }

    private void getBookingHistory() {
        binding.linearProgress.setVisibility(View.VISIBLE);

        db.collection("bookings")
                .whereEqualTo("uId", mAuth.getUid())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        bookingList.clear();
                        Log.d(TAG, task.getException() + " => ");
                        for (QueryDocumentSnapshot documentNew : task.getResult()) {
                            BookingInfo bookingInfo = documentNew.toObject(BookingInfo.class);
                            bookingInfo.setDocumentId(documentNew.getId());
                            bookingList.add(bookingInfo);
                        }
                        bookingsAdapter.notifyData(bookingList);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        bookingsAdapter.notifyData(bookingList);
                    }
                    binding.linearProgress.setVisibility(View.GONE);
                });
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
    public void onBookingClick(BookingInfo bookingInfo) {

    }

    @Override
    public void onPassengerInfo(BookingInfo bookingInfo) {
        PassengerInfoFragment passengerInfoFragment = PassengerInfoFragment.newInstance(bookingInfo);
        passengerInfoFragment.show(getSupportFragmentManager(), "PassengerInfoFragment");

    }
}

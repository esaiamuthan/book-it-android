package com.sampleapplication.bookit.history;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bookit.app.R;
import com.bookit.app.databinding.FragmentPassengerInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.sampleapplication.bookit.base.BaseBottomSheetDialogFragment;
import com.sampleapplication.bookit.booking.create.PassengersAdapter;
import com.sampleapplication.bookit.model.BookingInfo;
import com.sampleapplication.bookit.model.Passenger;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class PassengerInfoFragment extends BaseBottomSheetDialogFragment {

    private FragmentPassengerInfoBinding binding;
    BookingInfo bookingInfo;
    private String TAG = PassengerInfoFragment.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private ArrayList<Passenger> passengers = new ArrayList<>();
    PassengersAdapter passengersAdapter;

    public PassengerInfoFragment() {
        // Required empty public constructor
    }

    public static PassengerInfoFragment newInstance(BookingInfo bookingInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("bookingInfo", bookingInfo);
        PassengerInfoFragment passengerInfoFragment = new PassengerInfoFragment();
        passengerInfoFragment.setArguments(bundle);
        return passengerInfoFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            bookingInfo = (BookingInfo) getArguments().getSerializable("bookingInfo");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_passenger_info, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    @Override
    public void initUI() {
        mAuth = FirebaseAuth.getInstance();
        binding.rvPassengers.setLayoutManager(new LinearLayoutManager(getActivity()));
        passengersAdapter = new PassengersAdapter(passengers);
        binding.rvPassengers.setAdapter(passengersAdapter);

        getPassengerInfo();
    }

    private void getPassengerInfo() {
        binding.linearProgress.setVisibility(View.VISIBLE);
        db.collection("bookings")
                .document(bookingInfo.getDocumentId())
                .collection("passengers")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        passengers.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Passenger passenger = document.toObject(Passenger.class);
                            passengers.add(passenger);
                        }
                        passengersAdapter.notifyData(passengers);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                        passengersAdapter.notifyData(passengers);
                    }
                    binding.linearProgress.setVisibility(View.GONE);
                });
    }
}

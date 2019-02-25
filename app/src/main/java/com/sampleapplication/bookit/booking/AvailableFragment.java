package com.sampleapplication.bookit.booking;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bookit.app.R;
import com.bookit.app.databinding.FragmentAvailableBinding;
import com.sampleapplication.bookit.base.BaseFragment;
import com.sampleapplication.bookit.booking.adapter.AvailabilityAdapter;
import com.sampleapplication.bookit.model.SeatAvailable;
import com.sampleapplication.bookit.model.Station;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class AvailableFragment extends BaseFragment implements OnBookSelectListener {

    ArrayList<SeatAvailable> availableList = new ArrayList<>();

    FragmentAvailableBinding binding;

    OnBookingListener onBookingListener;

    public Station sourceStation;

    public AvailableFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        onBookingListener = (OnBookingListener) context;
    }

    @Override
    public void onDetach() {
        onBookingListener = null;
        super.onDetach();
    }

    public static AvailableFragment newInstance(ArrayList<SeatAvailable> availableList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("array", availableList);
        AvailableFragment availableFragment = new AvailableFragment();
        availableFragment.setArguments(bundle);
        return availableFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
            availableList = (ArrayList<SeatAvailable>) getArguments().getSerializable("array");
        sourceStation = ((BookingSearchActivity) Objects.requireNonNull(getActivity())).sourceStation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_available, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    @Override
    public void initUI() {
        binding.rvAvailable.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvAvailable.setAdapter(new AvailabilityAdapter(availableList, this, sourceStation));

        if (availableList.size() > 0) {
            binding.rvAvailable.setVisibility(View.VISIBLE);
            binding.noRecords.setVisibility(View.GONE);
        } else {
            binding.rvAvailable.setVisibility(View.GONE);
            binding.noRecords.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBookSelect(SeatAvailable seatAvailable) {
        onBookingListener.onBookClicked(seatAvailable);
    }

    public interface OnBookingListener {

        public void onBookClicked(SeatAvailable seatAvailable);
    }
}

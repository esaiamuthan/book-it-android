package com.sampleapplication.bookit.booking;


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
import com.bookit.app.databinding.FragmentUnAvailableBinding;
import com.sampleapplication.bookit.base.BaseFragment;
import com.sampleapplication.bookit.booking.adapter.AvailabilityAdapter;
import com.sampleapplication.bookit.model.SeatAvailable;
import com.sampleapplication.bookit.model.Station;

import java.util.ArrayList;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class UnAvailableFragment extends BaseFragment implements OnBookSelectListener {

    ArrayList<SeatAvailable> unAvailableList = new ArrayList<>();
    FragmentUnAvailableBinding binding;
    public Station sourceStation;

    public UnAvailableFragment() {
        // Required empty public constructor
    }

    public static UnAvailableFragment newInstance(ArrayList<SeatAvailable> unAvailableList) {
        Bundle bundle = new Bundle();
        bundle.putSerializable("array", unAvailableList);
        UnAvailableFragment unAvailableFragment = new UnAvailableFragment();
        unAvailableFragment.setArguments(bundle);
        return unAvailableFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            unAvailableList = (ArrayList<SeatAvailable>) getArguments().getSerializable("array");
        sourceStation = ((BookingSearchActivity) Objects.requireNonNull(getActivity())).sourceStation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_un_available, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    @Override
    public void initUI() {
        binding.rvUnAvailable.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rvUnAvailable.setAdapter(new AvailabilityAdapter(unAvailableList, this, sourceStation));


        if (unAvailableList.size() > 0) {
            binding.rvUnAvailable.setVisibility(View.VISIBLE);
            binding.noRecords.setVisibility(View.GONE);
        } else {
            binding.rvUnAvailable.setVisibility(View.GONE);
            binding.noRecords.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBookSelect(SeatAvailable seatAvailable) {

    }
}

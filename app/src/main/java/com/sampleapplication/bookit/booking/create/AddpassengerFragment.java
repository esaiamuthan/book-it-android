package com.sampleapplication.bookit.booking.create;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.bookit.app.R;
import com.bookit.app.databinding.FragmentAddpassengerBinding;
import com.sampleapplication.bookit.base.BaseBottomSheetDialogFragment;
import com.sampleapplication.bookit.booking.SearchActivity;
import com.sampleapplication.bookit.model.Passenger;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddpassengerFragment extends BaseBottomSheetDialogFragment {

    private FragmentAddpassengerBinding binding;
    OnAddPassengerListener onAddPassengerListener;

    public AddpassengerFragment() {
        // Required empty public constructor
    }

    public static AddpassengerFragment newInstance() {
        AddpassengerFragment addpassengerFragment = new AddpassengerFragment();
        return addpassengerFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
                inflater, R.layout.fragment_addpassenger, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initUI();
    }

    @Override
    public void initUI() {

        binding.spProof.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    binding.etProofId.setVisibility(View.GONE);
                } else {
                    binding.etProofId.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (binding.spProof.getSelectedItemPosition() == 0) {
            binding.etProofId.setVisibility(View.GONE);
        } else {
            binding.etProofId.setVisibility(View.VISIBLE);
        }

        binding.tvAdd.setOnClickListener(v -> {
            if (checkFields()) {
                Passenger passenger = new Passenger();
                passenger.setpName(binding.etName.getText().toString());
                passenger.setpAge(Integer.parseInt(binding.etAge.getText().toString()));
                passenger.setBerth(binding.spBerth.getSelectedItem().toString());
                passenger.setpBerthPosition(binding.spBerth.getSelectedItemPosition());
                passenger.setpGender(binding.spGender.getSelectedItemPosition());
                if (binding.spProof.getSelectedItemPosition() != 0) {
                    passenger.setpProof(binding.spProof.getSelectedItem().toString());
                    passenger.setpProofPosition(binding.spProof.getSelectedItemPosition());
                }
                onAddPassengerListener.onPassengerAdd(passenger);
                dismiss();
            }
        });
    }

    private boolean checkFields() {
        boolean validated = true;
        if (binding.etName.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter passenger name", Toast.LENGTH_SHORT).show();

            validated = false;
        } else if (binding.etAge.getText().toString().isEmpty()) {
            Toast.makeText(getActivity(), "Please enter passenger age", Toast.LENGTH_SHORT).show();

            validated = false;
        } else if (binding.spGender.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please select gender", Toast.LENGTH_SHORT).show();

            validated = false;
        } else if (binding.spBerth.getSelectedItemPosition() == 0) {
            Toast.makeText(getActivity(), "Please select berth", Toast.LENGTH_SHORT).show();

            validated = false;
        }
        return validated;
    }

    public void setListener(OnAddPassengerListener listener) {
        this.onAddPassengerListener = listener;
    }

    public interface OnAddPassengerListener {
        public void onPassengerAdd(Passenger passenger);
    }
}

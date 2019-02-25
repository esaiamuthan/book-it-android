package com.sampleapplication.bookit.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.bookit.app.R;
import com.bookit.app.databinding.ActivityHomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.booking.SearchActivity;
import com.sampleapplication.bookit.connection.LoginActivity;
import com.sampleapplication.bookit.history.BookingHistoryActivity;
import com.sampleapplication.bookit.profile.MyProfileActivity;


public class HomeActivity extends BaseActivity implements View.OnClickListener {

    private ActivityHomeBinding binding;

    private String TAG = HomeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        binding.contentHome.tvNewBooking.setOnClickListener(this);
        binding.contentHome.tvBookingHistory.setOnClickListener(this);
        binding.contentHome.tvMyProfile.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                FirebaseAuth.getInstance().signOut();

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvBookingHistory:
                startActivity(new Intent(this, BookingHistoryActivity.class));
                break;
            case R.id.tvNewBooking:
                startActivity(new Intent(this, SearchActivity.class));
                break;
            case R.id.tvMyProfile:
                startActivity(new Intent(this, MyProfileActivity.class));
                break;
        }
    }
}

package com.sampleapplication.bookit.home;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.sampleapplication.bookit.R;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.connection.LoginActivity;
import com.sampleapplication.bookit.databinding.ActivityHomeBinding;

public class HomeActivity extends BaseActivity {

    private ActivityHomeBinding binding;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    binding.message.setText(R.string.title_home);
                    return true;
                case R.id.navigation_dashboard:
                    binding.message.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    binding.message.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        binding.navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.tbHome);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
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

                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

                break;
        }
        return true;
    }
}

package com.sampleapplication.bookit;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bookit.app.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sampleapplication.bookit.connection.LoginActivity;
import com.sampleapplication.bookit.home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        new Handler().postDelayed(() -> {
            if (currentUser != null)
                startActivity(new Intent(MainActivity.this, HomeActivity.class));
            else
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
        }, 1000);
    }
}

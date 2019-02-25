package com.sampleapplication.bookit.profile;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bookit.app.R;
import com.bookit.app.databinding.ActivityMyProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sampleapplication.bookit.base.BaseActivity;
import com.sampleapplication.bookit.connection.LoginActivity;
import com.sampleapplication.bookit.profile.MyProfileActivity;
import com.sampleapplication.bookit.model.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyProfileActivity extends BaseActivity {

    private ActivityMyProfileBinding binding;

    private String TAG = MyProfileActivity.class.getSimpleName();

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    private FirebaseAuth mAuth;

    private User currentUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_profile);
        initUI();
    }

    @Override
    public void initUI() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        binding.linearProgress.setVisibility(View.VISIBLE);
        db.collection("bookit")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
            currentUserInfo = documentSnapshot.toObject(User.class);
            prefillData();
            binding.linearProgress.setVisibility(View.GONE);
        });
    }

    private void prefillData() {
        binding.etMobile.setText(currentUserInfo.getMobile());
        binding.etRName.setText(currentUserInfo.getName());
        binding.etREmail.setText(currentUserInfo.getEmail());
        binding.etREmail.setEnabled(false);
        binding.spGender.setSelection(currentUserInfo.getGender(), true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_register, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                hideKeyboard();
                finish();
                break;
            case R.id.action_submit:
                hideKeyboard();
                if (checkFields()) {
                    updateUser();
                }
                break;
        }
        return true;
    }


    private void updateUser() {
        binding.linearProgress.setVisibility(View.VISIBLE);

        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("name", binding.etRName.getText().toString());
        user.put("email", binding.etREmail.getText().toString());
        user.put("mobile", binding.etMobile.getText().toString());
        user.put("gender", binding.spGender.getSelectedItemPosition());

        db.collection("bookit")
                .document(Objects.requireNonNull(mAuth.getUid()))
                .update(user)
                .addOnSuccessListener(documentReference -> {
                    binding.linearProgress.setVisibility(View.GONE);
                    finish();
                })
                .addOnFailureListener(throable -> {
                    binding.linearProgress.setVisibility(View.GONE);

                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure" + throable.getMessage());
                    try {
                        throw Objects.requireNonNull(throable);
                    } catch (FirebaseAuthWeakPasswordException e) {
                        Toast.makeText(MyProfileActivity.this, "Weak password.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(MyProfileActivity.this, "Invalid email address.",
                                Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthUserCollisionException e) {
                        Toast.makeText(MyProfileActivity.this, "User already exists.",
                                Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MyProfileActivity.this, "" + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public boolean checkFields() {
        boolean validated = true;
        if (binding.etRName.getText().toString().isEmpty()) {
            binding.etRName.setError("Please enter user name");
            validated = false;
        } else if (binding.etMobile.getText().toString().isEmpty()) {
            binding.etMobile.setError("Please enter mobile number");
            validated = false;
        } else if (binding.etMobile.getText().toString().length() < 10) {
            binding.etMobile.setError("Please enter valid (10 digit) mobile number");
            validated = false;
        } else if (binding.etREmail.getText().toString().isEmpty()) {
            binding.etREmail.setError("Please enter email address");
            validated = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etREmail.getText().toString()).matches()) {
            binding.etREmail.setError("Please enter valid email address");
            validated = false;
        } else if (binding.spGender.getSelectedItemPosition() == 0) {
            showMessage("Please select your gender");
            validated = false;
        }
        return validated;
    }
}

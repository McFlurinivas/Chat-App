package com.example.harinivaschatapp;

import android.content.Intent;
import android.os.Bundle;


import com.example.harinivaschatapp.databinding.ActivitySignupBinding;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Toast;

import com.example.authentication.AuthenticationService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SignupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.signupButton.setOnClickListener(
            (view) -> {
                final String email = binding.emailEditText.getText().toString();
                final String password = binding.passwordEditText.getText().toString();

                CompletableFuture.runAsync(
                    () -> {
                        try {
                            authenticationService.signup(email, password);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                            runOnUiThread(
                                () -> {
                                    Toast.makeText(this, "Signup failed", Toast.LENGTH_LONG).show();
                                }
                            );
                            Log.e("CustomLog", "Signup failed");

                            return;
                        }

                        Log.d("CustomLog", "Signup successful");

                        runOnUiThread(
                            () -> {
                                Toast.makeText(this, "Signup successful", Toast.LENGTH_LONG).show();
                            }
                        );
                        openHomeScreen();
                    }
                );
            }
        );
    }

    private void openHomeScreen() {
        final Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }



    private ActivitySignupBinding binding;

    private final AuthenticationService authenticationService = new AuthenticationService();
}
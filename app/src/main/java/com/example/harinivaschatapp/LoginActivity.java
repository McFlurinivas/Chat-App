package com.example.harinivaschatapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.harinivaschatapp.databinding.ActivityLoginBinding;

import com.example.authentication.AuthenticationService;

import java.util.concurrent.CompletableFuture;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(authenticationService.getCurrentUser() != null) {
            openHomeScreen();
            return;
        }

        final ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginButton.setOnClickListener(
            (view) -> {
                final String email = binding.emailEditText.getText().toString();
                final String password = binding.passwordEditText.getText().toString();

                CompletableFuture.runAsync(
                    () -> {
                        try {
                            authenticationService.login(email, password);
                        }
                        catch(Exception e) {
                            e.printStackTrace();

                            Log.d("CustomLog", "Login failed");
                            runOnUiThread(
                                () -> {
                                    Toast.makeText(this, "Login failed", Toast.LENGTH_LONG).show();
                                }
                            );

                            return;
                        }

                        Log.d("CustomLog", "Logged in successfully");

                        runOnUiThread(
                            () -> {
                                Toast.makeText(this, "Logged in successfully", Toast.LENGTH_LONG)
                                    .show();
                            }
                        );

                        openHomeScreen();
                    }
                );
            }
        );

        binding.signupButton.setOnClickListener((view) -> openSignupScreen());
    }

    private void openSignupScreen() {
        final Intent intent = new Intent(this, SignupActivity.class);
        startActivity(intent);
    }

    private void openHomeScreen() {
        final Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
    }

    private final AuthenticationService authenticationService = new AuthenticationService();
}
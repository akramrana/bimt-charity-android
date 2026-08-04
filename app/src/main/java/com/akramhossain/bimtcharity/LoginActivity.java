package com.akramhossain.bimtcharity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.akramhossain.bimtcharity.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnLogin.setOnClickListener(view -> validateLogin());
    }

    private void validateLogin() {
        String email = binding.etEmail.getText() == null
                ? ""
                : binding.etEmail.getText().toString().trim();

        String password = binding.etPassword.getText() == null
                ? ""
                : binding.etPassword.getText().toString();

        binding.emailLayout.setError(null);
        binding.passwordLayout.setError(null);

        boolean isValid = true;

        if (TextUtils.isEmpty(email)) {
            binding.emailLayout.setError("Email is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            binding.passwordLayout.setError("Password is required");
            isValid = false;
        }

        if (!isValid) {
            return;
        }

        // Temporary login until API is ready
        if (
                email.equalsIgnoreCase("member@bimt.org")
                        && password.equals("123456")
        ) {
            openDashboard();
        } else {
            Toast.makeText(
                    this,
                    "Invalid email or password",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    private void openDashboard() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
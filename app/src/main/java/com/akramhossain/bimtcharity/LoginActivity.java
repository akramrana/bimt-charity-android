package com.akramhossain.bimtcharity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.akramhossain.bimtcharity.databinding.ActivityLoginBinding;
import com.akramhossain.bimtcharity.models.LoginRequest;
import com.akramhossain.bimtcharity.models.LoginResponse;
import com.akramhossain.bimtcharity.network.ApiClient;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean loggedIn = getSharedPreferences(
                "bimt_session",
                MODE_PRIVATE
        ).getBoolean("logged_in", false);

        if (loggedIn) {
            openDashboard();
            return;
        }

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


        performLogin(email, password);
    }

    private void performLogin(String email, String password) {
        setLoading(true);

        LoginRequest request = new LoginRequest(email, password);

        ApiClient.getApiService()
                .login(request)
                .enqueue(new Callback<LoginResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<LoginResponse> call,
                            @NonNull Response<LoginResponse> response
                    ) {
                        setLoading(false);

                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Login failed. HTTP " + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        LoginResponse loginResponse = response.body();

                        if (loginResponse == null) {
                            Toast.makeText(
                                    LoginActivity.this,
                                    "Empty response from server",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        if (loginResponse.isSuccess()
                                && loginResponse.getStatus() == 200
                                && loginResponse.getData() != null) {

                            saveSession(loginResponse.getData());
                            openDashboard();

                        } else {
                            String message = loginResponse.getMessage();

                            if (message == null || message.trim().isEmpty()) {
                                message = "Invalid email or password";
                            }

                            Toast.makeText(
                                    LoginActivity.this,
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<LoginResponse> call,
                            @NonNull Throwable throwable
                    ) {
                        setLoading(false);

                        Toast.makeText(
                                LoginActivity.this,
                                "Connection failed: " + throwable.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        binding.loginProgress.setVisibility(loading ? View.VISIBLE : View.GONE);

        binding.btnLogin.setEnabled(!loading);
        binding.etEmail.setEnabled(!loading);
        binding.etPassword.setEnabled(!loading);

        binding.btnLogin.setText(loading ? "Logging in..." : "Login");
    }

    private void saveSession(LoginResponse.MemberData member) {
        getSharedPreferences("bimt_session", MODE_PRIVATE)
                .edit()
                .putBoolean("logged_in", true)
                .putString("member_id", member.getId())
                .putString("fullname", member.getFullname())
                .putString("member_code", member.getMemberCode())
                .putString("email", member.getEmail())
                .putString("image", member.getImage())
                .putString("phone", member.getPhone())
                .putString("user_type", member.getUserType())
                .putFloat(
                        "recurring_amount",
                        (float) member.getRecurringAmount()
                )
                .putString("created_at", member.getCreatedAt())
                .putString("batch", member.getBatch())
                .putString("department", member.getDepartment())
                .putString("address", member.getAddress())
                .apply();
    }

    private void openDashboard() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }
}
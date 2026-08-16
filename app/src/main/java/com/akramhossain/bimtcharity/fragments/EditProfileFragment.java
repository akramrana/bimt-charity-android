package com.akramhossain.bimtcharity.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akramhossain.bimtcharity.R;
import com.akramhossain.bimtcharity.databinding.FragmentEditProfileBinding;
import com.akramhossain.bimtcharity.models.EditProfileRequest;
import com.akramhossain.bimtcharity.models.EditProfileResponse;
import com.akramhossain.bimtcharity.models.UserResponse;
import com.akramhossain.bimtcharity.network.ApiClient;
import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileFragment extends Fragment {
    private static final String SESSION_NAME = "bimt_session";
    private FragmentEditProfileBinding binding;
    private String selectedImageBase64;
    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    this::handleSelectedImage
            );

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentEditProfileBinding.inflate(
                inflater,
                container,
                false
        );
        populateUser();
        binding.btnChooseImage.setOnClickListener(view ->
                imagePicker.launch("image/*")
        );
        binding.btnSave.setOnClickListener(view ->
                validateAndSubmit()
        );
        return binding.getRoot();
    }

    /**
     * Load currently logged-in user's information
     * directly from SharedPreferences session.
     */
    private void populateUser() {
        SharedPreferences session = requireContext()
                .getSharedPreferences(
                        SESSION_NAME,
                        Context.MODE_PRIVATE
                );
        binding.etFullname.setText(
                session.getString("fullname", "")
        );
        binding.etAddress.setText(
                session.getString("address", "")
        );
        binding.etBatch.setText(
                session.getString("batch", "")
        );
        binding.etDepartment.setText(
                session.getString("department", "")
        );
        String profileImage = session.getString("image", "");

        loadProfileImage(profileImage);
        /*
         * If you have TextViews/EditTexts for these fields
         * in your XML, you can also load them:
         *
         * String email = session.getString("email", "");
         * String phone = session.getString("phone", "");
         * String memberCode = session.getString("member_code", "");
         */
    }

    private void loadProfileImage(String imageUrl) {

        if (TextUtils.isEmpty(imageUrl)) {
            binding.profileImage.setImageResource(R.drawable.ic_bimt_logo);
            return;
        }

        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_bimt_logo)
                .error(R.drawable.ic_bimt_logo)
                .into(binding.profileImage);
    }

    private void handleSelectedImage(Uri uri) {
        if (uri == null || binding == null) {
            return;
        }
        try {
            selectedImageBase64 = readBase64(uri);
            binding.profileImage.setImageURI(uri);
        } catch (IOException exception) {
            selectedImageBase64 = null;
            Toast.makeText(
                    requireContext(),
                    "Unable to read selected image",
                    Toast.LENGTH_LONG
            ).show();
        }
    }

    private String readBase64(Uri uri) throws IOException {
        try (
                InputStream input = requireContext()
                        .getContentResolver()
                        .openInputStream(uri);

                ByteArrayOutputStream output =
                        new ByteArrayOutputStream()
        ) {
            if (input == null) {
                throw new IOException(
                        "Image stream is unavailable"
                );
            }
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) {
                output.write(buffer, 0, count);
            }
            return Base64.encodeToString(
                    output.toByteArray(),
                    Base64.NO_WRAP
            );
        }
    }

    private void validateAndSubmit() {
        clearErrors();
        String fullname = textOf(binding.etFullname);
        String address = textOf(binding.etAddress);
        String batch = textOf(binding.etBatch);
        String department = textOf(binding.etDepartment);
        String password = textOf(binding.etPassword);
        String confirmPassword = textOf(binding.etConfirmPassword);
        boolean valid = true;
        if (TextUtils.isEmpty(fullname)) {

            binding.fullnameLayout.setError(
                    "Full name is required"
            );

            valid = false;
        }
        /*
         * Password is optional.
         *
         * But if user enters password,
         * confirmation must match.
         */
        if (!password.equals(confirmPassword)) {

            binding.confirmPasswordLayout.setError(
                    "Passwords do not match"
            );
            valid = false;
        }
        if (!valid) {
            return;
        }
        int userId = getLoggedInUserId();
        if (userId <= 0) {
            Toast.makeText(
                    requireContext(),
                    "User information is unavailable",
                    Toast.LENGTH_LONG
            ).show();
            return;
        }
        EditProfileRequest request =
                new EditProfileRequest(
                        userId,
                        fullname,
                        address,
                        batch,
                        department,
                        password.isEmpty() ? null : password,
                        confirmPassword.isEmpty()
                                ? null
                                : confirmPassword,
                        selectedImageBase64
                );

        submit(
                request
        );
    }

    /**
     * Get logged-in user/member ID from session.
     */
    private int getLoggedInUserId() {
        SharedPreferences session = requireContext()
                .getSharedPreferences(
                        SESSION_NAME,
                        Context.MODE_PRIVATE
                );
        String memberId = session.getString(
                "member_id",
                ""
        );
        if (memberId == null ||
                memberId.trim().isEmpty()) {
            return 0;
        }
        try {
            return Integer.parseInt(memberId);
        } catch (NumberFormatException exception) {

            return 0;
        }
    }

    private void submit(EditProfileRequest request) {
        setLoading(true);
        ApiClient.getApiService()
                .editProfile(request)
                .enqueue(new Callback<EditProfileResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<EditProfileResponse> call,
                            @NonNull Response<EditProfileResponse> response
                    ) {
                        if (binding == null) {
                            return;
                        }
                        setLoading(false);
                        EditProfileResponse result = response.body();
                        if (!response.isSuccessful()
                                || result == null
                                || !result.isSuccess()) {

                            String message = result == null
                                    ? "Unable to update profile"
                                    : result.getMessage();

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        if (result.getData() != null) {
                            updateSession(result.getData());
                            binding.etFullname.setText(result.getData().getFullname());
                            binding.etAddress.setText(result.getData().getAddress());
                            binding.etBatch.setText(result.getData().getBatch());
                            binding.etDepartment.setText(result.getData().getDepartment());
                            loadProfileImage(result.getData().getImage());
                        }

                        binding.etPassword.setText("");
                        binding.etConfirmPassword.setText("");
                        selectedImageBase64 = null;
                        String message = result.getMessage();
                        if (TextUtils.isEmpty(message)) {
                            message = "Profile updated successfully";
                        }
                        Toast.makeText(
                                requireContext(),
                                message,
                                Toast.LENGTH_LONG
                        ).show();
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<EditProfileResponse> call,
                            @NonNull Throwable throwable
                    ) {
                        if (binding == null) {
                            return;
                        }
                        setLoading(false);
                        Toast.makeText(
                                requireContext(),
                                "Connection failed: " + throwable.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    /**
     * Keep session synchronized with updated profile.
     */
    private void updateSession(EditProfileResponse.Data data) {

        requireContext()
                .getSharedPreferences(
                        SESSION_NAME,
                        Context.MODE_PRIVATE
                )
                .edit()
                .putString("fullname", data.getFullname())
                .putString("address", data.getAddress())
                .putString("batch", data.getBatch())
                .putString("department", data.getDepartment())
                .putString("image", data.getImage())
                .apply();
    }

    private void clearErrors() {
        binding.fullnameLayout.setError(null);
        binding.confirmPasswordLayout.setError(null);
    }

    private String textOf(
            android.widget.EditText editText
    ) {
        if (editText.getText() == null) {
            return "";
        }
        return editText
                .getText()
                .toString()
                .trim();
    }

    private void setLoading(boolean loading) {
        if (binding == null) {
            return;
        }
        binding.progressBar.setVisibility(
                loading
                        ? View.VISIBLE
                        : View.GONE
        );
        binding.btnSave.setEnabled(!loading);
        binding.btnChooseImage.setEnabled(!loading);
        binding.btnSave.setText(
                loading
                        ? "Updating..."
                        : "Update profile"
        );
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
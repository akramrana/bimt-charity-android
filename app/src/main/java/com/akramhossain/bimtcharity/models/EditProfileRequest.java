package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

public class EditProfileRequest {

    @SerializedName("user_id")
    private final int userId;
    private final String fullname;
    private final String address;
    private final String batch;
    private final String department;
    private final String password;
    @SerializedName("confirm_password")
    private final String confirmPassword;
    private final String image;

    public EditProfileRequest(
            int userId,
            String fullname,
            String address,
            String batch,
            String department,
            String password,
            String confirmPassword,
            String image
    ) {
        this.userId = userId;
        this.fullname = fullname;
        this.address = address;
        this.batch = batch;
        this.department = department;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.image = image;
    }
}

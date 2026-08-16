package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

public class EditProfileResponse {

    private boolean success;
    private int status;
    private String message;
    private Data data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public Data getData() {
        return data;
    }

    public static class Data {

        @SerializedName("user_id")
        private String userId;

        private String fullname;
        private String address;
        private String batch;
        private String department;
        private String image;

        public String getUserId() {
            return userId;
        }

        public String getFullname() {
            return fullname;
        }

        public String getAddress() {
            return address;
        }

        public String getBatch() {
            return batch;
        }

        public String getDepartment() {
            return department;
        }

        public String getImage() {
            return image;
        }
    }
}
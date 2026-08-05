package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {

    private boolean success;
    private int status;
    private String message;
    private MemberData data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public MemberData getData() {
        return data;
    }

    public static class MemberData {

        private String id;

        private String fullname;

        @SerializedName("member_code")
        private String memberCode;

        private String email;
        private String image;
        private String phone;

        @SerializedName("user_type")
        private String userType;

        @SerializedName("recurring_amount")
        private double recurringAmount;

        @SerializedName("created_at")
        private String createdAt;

        private String batch;
        private String department;

        public String getId() {
            return id;
        }

        public String getFullname() {
            return fullname;
        }

        public String getMemberCode() {
            return memberCode;
        }

        public String getEmail() {
            return email;
        }

        public String getImage() {
            return image;
        }

        public String getPhone() {
            return phone;
        }

        public String getUserType() {
            return userType;
        }

        public double getRecurringAmount() {
            return recurringAmount;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getBatch() {
            return batch;
        }

        public String getDepartment() {
            return department;
        }
    }
}
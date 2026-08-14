package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserResponse {

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

        private List<User> dataProvider;
        private Pagination pagination;

        public List<User> getDataProvider() {
            return dataProvider;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class User {

        @SerializedName("user_id")
        private int userId;

        @SerializedName("member_code")
        private String memberCode;

        private String fullname;
        private String image;
        private String email;
        private String phone;

        @SerializedName("alt_phone")
        private String altPhone;

        private String address;
        private String batch;
        private String department;

        @SerializedName("enable_login")
        private int enableLogin;

        @SerializedName("user_type")
        private String userType;

        @SerializedName("recurring_amount")
        private double recurringAmount;

        @SerializedName("currency_id")
        private int currencyId;

        @SerializedName("invited_user_id")
        private int invitedUserId;

        @SerializedName("is_active")
        private int isActive;

        @SerializedName("is_deleted")
        private int isDeleted;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("is_exception")
        private int isException;

        @SerializedName("is_approved")
        private int isApproved;

        @SerializedName("is_active_donor")
        private int isActiveDonor;

        @SerializedName("invited_user")
        private String invitedUser;

        @SerializedName("currency_code")
        private String currencyCode;

        public int getUserId() {
            return userId;
        }

        public String getMemberCode() {
            return memberCode;
        }

        public String getFullname() {
            return fullname;
        }

        public String getImage() {
            return image;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getAltPhone() {
            return altPhone;
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

        public int getEnableLogin() {
            return enableLogin;
        }

        public String getUserType() {
            return userType;
        }

        public double getRecurringAmount() {
            return recurringAmount;
        }

        public int getCurrencyId() {
            return currencyId;
        }

        public int getInvitedUserId() {
            return invitedUserId;
        }

        public int getIsActive() {
            return isActive;
        }

        public int getIsDeleted() {
            return isDeleted;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public int getIsException() {
            return isException;
        }

        public int getIsApproved() {
            return isApproved;
        }

        public int getIsActiveDonor() {
            return isActiveDonor;
        }

        public String getInvitedUser() {
            return invitedUser;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }
    }

    public static class Pagination {

        private int page;
        private int pageCount;
        private int totalCount;
        private int pageSize;

        public int getPage() {
            return page;
        }

        public int getPageCount() {
            return pageCount;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int getPageSize() {
            return pageSize;
        }
    }
}

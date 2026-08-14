package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FundRequestResponse {

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

        private List<FundRequest> dataProvider;
        private Pagination pagination;

        public List<FundRequest> getDataProvider() {
            return dataProvider;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class FundRequest {

        @SerializedName("fund_request_id")
        private int fundRequestId;

        @SerializedName("fund_request_number")
        private String fundRequestNumber;

        @SerializedName("request_user_id")
        private int requestUserId;

        private String title;

        @SerializedName("request_description")
        private String requestDescription;

        private String reason;

        @SerializedName("receiver_contact_details")
        private String receiverContactDetails;

        @SerializedName("investigation_information")
        private String investigationInformation;

        @SerializedName("fund_receiver_account_details")
        private String fundReceiverAccountDetails;

        @SerializedName("additional_information")
        private String additionalInformation;

        @SerializedName("request_amount")
        private double requestAmount;

        @SerializedName("currency_id")
        private int currencyId;

        private String file;

        @SerializedName("is_active")
        private int isActive;

        @SerializedName("is_deleted")
        private int isDeleted;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("request_user")
        private String requestUser;

        @SerializedName("currency_code")
        private String currencyCode;

        @SerializedName("approval_status")
        private String approvalStatus;

        public int getFundRequestId() {
            return fundRequestId;
        }

        public String getFundRequestNumber() {
            return fundRequestNumber;
        }

        public int getRequestUserId() {
            return requestUserId;
        }

        public String getTitle() {
            return title;
        }

        public String getRequestDescription() {
            return requestDescription;
        }

        public String getReason() {
            return reason;
        }

        public String getReceiverContactDetails() {
            return receiverContactDetails;
        }

        public String getInvestigationInformation() {
            return investigationInformation;
        }

        public String getFundReceiverAccountDetails() {
            return fundReceiverAccountDetails;
        }

        public String getAdditionalInformation() {
            return additionalInformation;
        }

        public double getRequestAmount() {
            return requestAmount;
        }

        public int getCurrencyId() {
            return currencyId;
        }

        public String getFile() {
            return file;
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

        public String getRequestUser() {
            return requestUser;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }

        public String getApprovalStatus() {
            return approvalStatus;
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

package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentReleaseResponse {

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

        private List<PaymentRelease> dataProvider;
        private Pagination pagination;

        public List<PaymentRelease> getDataProvider() {
            return dataProvider;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class PaymentRelease {

        @SerializedName("payment_release_id")
        private int paymentReleaseId;

        @SerializedName("release_invoice_number")
        private String releaseInvoiceNumber;

        @SerializedName("fund_request_id")
        private int fundRequestId;

        @SerializedName("release_by")
        private String releaseBy;

        private double amount;
        private String note;

        @SerializedName("currency_id")
        private int currencyId;

        @SerializedName("is_deleted")
        private int isDeleted;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("fund_request")
        private String fundRequest;

        @SerializedName("currency_code")
        private String currencyCode;

        public int getPaymentReleaseId() {
            return paymentReleaseId;
        }

        public String getReleaseInvoiceNumber() {
            return releaseInvoiceNumber;
        }

        public int getFundRequestId() {
            return fundRequestId;
        }

        public String getReleaseBy() {
            return releaseBy;
        }

        public double getAmount() {
            return amount;
        }

        public String getNote() {
            return note;
        }

        public int getCurrencyId() {
            return currencyId;
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

        public String getFundRequest() {
            return fundRequest;
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

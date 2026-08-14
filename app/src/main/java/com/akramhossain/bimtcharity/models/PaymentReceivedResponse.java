package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentReceivedResponse {

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

        private SearchModel searchModel;
        private List<PaymentReceived> dataProvider;
        private Pagination pagination;

        public SearchModel getSearchModel() {
            return searchModel;
        }

        public List<PaymentReceived> getDataProvider() {
            return dataProvider;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class SearchModel {

        @SerializedName("receiver_name")
        private String receiverName;

        @SerializedName("donated_by")
        private String donatedBy;

        @SerializedName("currency_code")
        private String currencyCode;

        public String getReceiverName() {
            return receiverName;
        }

        public String getDonatedBy() {
            return donatedBy;
        }

        public String getCurrencyCode() {
            return currencyCode;
        }
    }

    public static class PaymentReceived {

        @SerializedName("payment_received_id")
        private int paymentReceivedId;

        @SerializedName("received_invoice_number")
        private String receivedInvoiceNumber;

        @SerializedName("donated_by")
        private String donatedBy;

        @SerializedName("received_by")
        private int receivedBy;

        private String comments;
        private double amount;

        @SerializedName("instalment_month")
        private String instalmentMonth;

        @SerializedName("instalment_year")
        private String instalmentYear;

        @SerializedName("has_invoice")
        private int hasInvoice;

        @SerializedName("monthly_invoice_id")
        private int monthlyInvoiceId;

        @SerializedName("monthly_invoice_number")
        private String monthlyInvoiceNumber;

        @SerializedName("received_date")
        private String receivedDate;

        @SerializedName("currency_id")
        private int currencyId;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("is_deleted")
        private int isDeleted;

        private String file;

        @SerializedName("receiver_name")
        private String receiverName;

        @SerializedName("currency_code")
        private String currencyCode;

        public int getPaymentReceivedId() {
            return paymentReceivedId;
        }

        public String getReceivedInvoiceNumber() {
            return receivedInvoiceNumber;
        }

        public String getDonatedBy() {
            return donatedBy;
        }

        public int getReceivedBy() {
            return receivedBy;
        }

        public String getComments() {
            return comments;
        }

        public double getAmount() {
            return amount;
        }

        public String getInstalmentMonth() {
            return instalmentMonth;
        }

        public String getInstalmentYear() {
            return instalmentYear;
        }

        public int getHasInvoice() {
            return hasInvoice;
        }

        public int getMonthlyInvoiceId() {
            return monthlyInvoiceId;
        }

        public String getMonthlyInvoiceNumber() {
            return monthlyInvoiceNumber;
        }

        public String getReceivedDate() {
            return receivedDate;
        }

        public int getCurrencyId() {
            return currencyId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public int getIsDeleted() {
            return isDeleted;
        }

        public String getFile() {
            return file;
        }

        public String getReceiverName() {
            return receiverName;
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

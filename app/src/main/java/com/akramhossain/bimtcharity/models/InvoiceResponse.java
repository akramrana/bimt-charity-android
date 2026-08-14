package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class InvoiceResponse {

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

        private List<Invoice> dataProvider;
        private Pagination pagination;

        public List<Invoice> getDataProvider() {
            return dataProvider;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class Invoice {

        @SerializedName("monthly_invoice_id")
        private int monthlyInvoiceId;

        @SerializedName("monthly_invoice_number")
        private String monthlyInvoiceNumber;

        @SerializedName("receiver_id")
        private int receiverId;

        private double amount;

        @SerializedName("instalment_month")
        private String instalmentMonth;

        @SerializedName("instalment_year")
        private String instalmentYear;

        @SerializedName("currency_id")
        private int currencyId;

        @SerializedName("is_paid")
        private int isPaid;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("receiver_name")
        private String receiverName;

        @SerializedName("currency_code")
        private String currencyCode;

        public int getMonthlyInvoiceId() {
            return monthlyInvoiceId;
        }

        public String getMonthlyInvoiceNumber() {
            return monthlyInvoiceNumber;
        }

        public int getReceiverId() {
            return receiverId;
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

        public int getCurrencyId() {
            return currencyId;
        }

        public int getIsPaid() {
            return isPaid;
        }

        public String getCreatedAt() {
            return createdAt;
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
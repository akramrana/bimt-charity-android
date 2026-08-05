package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DashboardResponse {

    private boolean success;
    private int status;
    private String message;
    private DashboardData data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public DashboardData getData() {
        return data;
    }

    public static class DashboardData {

        private String users;

        @SerializedName("monthly_invoice")
        private String monthlyInvoice;

        @SerializedName("payment_received")
        private String paymentReceived;

        @SerializedName("payment_release")
        private String paymentRelease;

        private String expenses;

        @SerializedName("fund_request")
        private String fundRequest;

        private List<FundStat> stats;

        public String getUsers() {
            return users;
        }

        public String getMonthlyInvoice() {
            return monthlyInvoice;
        }

        public String getPaymentReceived() {
            return paymentReceived;
        }

        public String getPaymentRelease() {
            return paymentRelease;
        }

        public String getExpenses() {
            return expenses;
        }

        public String getFundRequest() {
            return fundRequest;
        }

        public List<FundStat> getStats() {
            return stats;
        }
    }

    public static class FundStat {

        private String name;
        private String amount;

        @SerializedName("fund_request_count")
        private String fundRequestCount;

        @SerializedName("fund_stat_curr_wise")
        private List<CurrencyStat> currencyStats;

        public String getName() {
            return name;
        }

        public String getAmount() {
            return amount;
        }

        public String getFundRequestCount() {
            return fundRequestCount;
        }

        public List<CurrencyStat> getCurrencyStats() {
            return currencyStats;
        }
    }

    public static class CurrencyStat {

        private String amount;

        @SerializedName("status_id")
        private String statusId;

        private String name;
        private String code;

        public String getAmount() {
            return amount;
        }

        public String getStatusId() {
            return statusId;
        }

        public String getName() {
            return name;
        }

        public String getCode() {
            return code;
        }
    }
}
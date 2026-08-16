package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

public class UnpaidInvoice {
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
    @SerializedName("receiver_name")
    private String receiverName;
    @SerializedName("currency_code")
    private String currencyCode;

    public int getMonthlyInvoiceId() { return monthlyInvoiceId; }
    public String getMonthlyInvoiceNumber() { return monthlyInvoiceNumber; }
    public int getReceiverId() { return receiverId; }
    public double getAmount() { return amount; }
    public String getInstalmentMonth() { return instalmentMonth; }
    public String getInstalmentYear() { return instalmentYear; }
    public int getCurrencyId() { return currencyId; }
    public String getReceiverName() { return receiverName; }
    public String getCurrencyCode() { return currencyCode; }

    @Override
    public String toString() {
        return "Invoice #" + monthlyInvoiceNumber + " — "
                + instalmentMonth + " " + instalmentYear + " — "
                + currencyCode + " " + formatAmount(amount);
    }

    private String formatAmount(double value) {
        if (value == Math.rint(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}

package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

public class PaymentSubmitRequest {
    @SerializedName("user_id")
    private final int userId;
    @SerializedName("received_date")
    private final String receivedDate;
    private final String comments;
    @SerializedName("has_invoice")
    private final int hasInvoice;
    @SerializedName("monthly_invoice_id")
    private final Integer monthlyInvoiceId;
    private final Double amount;
    @SerializedName("currency_id")
    private final Integer currencyId;
    @SerializedName("instalment_month")
    private final String instalmentMonth;
    @SerializedName("instalment_year")
    private final String instalmentYear;
    private final String file;

    private PaymentSubmitRequest(int userId, String receivedDate, String comments,
                                 int hasInvoice, Integer monthlyInvoiceId, Double amount,
                                 Integer currencyId, String instalmentMonth,
                                 String instalmentYear, String file) {
        this.userId = userId;
        this.receivedDate = receivedDate;
        this.comments = comments;
        this.hasInvoice = hasInvoice;
        this.monthlyInvoiceId = monthlyInvoiceId;
        this.amount = amount;
        this.currencyId = currencyId;
        this.instalmentMonth = instalmentMonth;
        this.instalmentYear = instalmentYear;
        this.file = file;
    }

    public static PaymentSubmitRequest againstInvoice(int userId, String date,
            String comments, int invoiceId, String file) {
        return new PaymentSubmitRequest(userId, date, comments, 1, invoiceId,
                null, null, null, null, file);
    }

    public static PaymentSubmitRequest withoutInvoice(int userId, String date,
            String comments, double amount, int currencyId, String month,
            String year, String file) {
        return new PaymentSubmitRequest(userId, date, comments, 0, null,
                amount, currencyId, month, year, file);
    }
}

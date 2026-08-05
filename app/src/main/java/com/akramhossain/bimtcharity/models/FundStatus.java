package com.akramhossain.bimtcharity.models;

public class FundStatus {

    private final String name;
    private final String count;
    private final String amount;
    private final String currency;

    public FundStatus(
            String name,
            String count,
            String amount,
            String currency
    ) {
        this.name = name;
        this.count = count;
        this.amount = amount;
        this.currency = currency;
    }

    public String getName() {
        return name;
    }

    public String getCount() {
        return count;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }
}
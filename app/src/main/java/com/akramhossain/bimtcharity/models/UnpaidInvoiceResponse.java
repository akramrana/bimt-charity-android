package com.akramhossain.bimtcharity.models;

import java.util.List;

public class UnpaidInvoiceResponse {
    private boolean success;
    private int status;
    private String message;
    private Data data;

    public boolean isSuccess() { return success; }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        private List<UnpaidInvoice> dataProvider;
        public List<UnpaidInvoice> getDataProvider() { return dataProvider; }
    }
}

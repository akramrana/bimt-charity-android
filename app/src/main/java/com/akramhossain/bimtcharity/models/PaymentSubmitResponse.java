package com.akramhossain.bimtcharity.models;

public class PaymentSubmitResponse {
    private boolean success;
    private int status;
    private String message;
    private Object data;

    public boolean isSuccess() { return success; }
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}

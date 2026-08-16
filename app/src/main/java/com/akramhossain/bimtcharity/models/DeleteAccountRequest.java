package com.akramhossain.bimtcharity.models;

public class DeleteAccountRequest {

    private String user_id;

    public DeleteAccountRequest(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
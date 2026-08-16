package com.akramhossain.bimtcharity.models;

public class CmsResponse {

    private boolean success;
    private int status;
    private String message;
    private CmsData data;

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public CmsData getData() {
        return data;
    }

    public static class CmsData {

        private int id;
        private String title;
        private String content;

        public int getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getContent() {
            return content;
        }
    }
}

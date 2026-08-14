package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class DocumentResponse {

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

        private List<Document> dataProvider;
        private Pagination pagination;

        public List<Document> getDataProvider() {
            return dataProvider;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class Document {

        @SerializedName("document_id")
        private int documentId;

        private String title;
        private String description;
        private String file;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("is_deleted")
        private int isDeleted;

        private String user;

        public int getDocumentId() {
            return documentId;
        }

        public String getTitle() {
            return title;
        }

        public String getDescription() {
            return description;
        }

        public String getFile() {
            return file;
        }

        public int getUserId() {
            return userId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getIsDeleted() {
            return isDeleted;
        }

        public String getUser() {
            return user;
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

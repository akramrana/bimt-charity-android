package com.akramhossain.bimtcharity.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NotificationResponse {

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

        private List<Notification> dataProvider;
        private Pagination pagination;

        public List<Notification> getDataProvider() {
            return dataProvider;
        }

        public Pagination getPagination() {
            return pagination;
        }
    }

    public static class Notification {

        @SerializedName("notification_id")
        private int notificationId;

        private String type;

        @SerializedName("type_id")
        private int typeId;

        private String comments;

        @SerializedName("added_by")
        private int addedBy;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("is_deleted")
        private int isDeleted;

        public int getNotificationId() {
            return notificationId;
        }

        public String getType() {
            return type;
        }

        public int getTypeId() {
            return typeId;
        }

        public String getComments() {
            return comments;
        }

        public int getAddedBy() {
            return addedBy;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public int getIsDeleted() {
            return isDeleted;
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

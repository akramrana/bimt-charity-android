package com.akramhossain.bimtcharity.models;

public class PushTokenRequest {

    private String userId;
    private String deviceId;
    private String pushType;
    private String pushToken;

    public PushTokenRequest(
            String userId,
            String deviceId,
            String pushType,
            String pushToken
    ) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.pushType = pushType;
        this.pushToken = pushToken;
    }

    public String getUserId() {
        return userId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public String getPushType() {
        return pushType;
    }

    public String getPushToken() {
        return pushToken;
    }
}
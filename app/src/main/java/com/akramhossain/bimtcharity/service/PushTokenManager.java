package com.akramhossain.bimtcharity.service;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.akramhossain.bimtcharity.network.ApiClient;
import com.akramhossain.bimtcharity.network.ApiService;
import com.akramhossain.bimtcharity.models.PushTokenRequest;
import com.akramhossain.bimtcharity.models.PushTokenResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PushTokenManager {

    private static final String TAG = "PushTokenManager";

    public static void sendTokenToServer(
            Context context,
            String token,
            String pushType
    ) {

        String androidId = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        String userId = context.getApplicationContext()
                .getSharedPreferences(
                        "bimt_session",
                        Context.MODE_PRIVATE
                )
                .getString("member_id", "");

        if (userId == null || userId.trim().isEmpty()) {
            Log.w(TAG, "Member information not found");
            return;
        }

        if (token == null || token.trim().isEmpty()) {
            Log.w(TAG, "Push token is empty");
            return;
        }

        PushTokenRequest request = new PushTokenRequest(
                userId,
                androidId,
                pushType,
                token
        );

        ApiClient.getApiService()
                .saveDeviceToken(request)
                .enqueue(new Callback<PushTokenResponse>() {

                    @Override
                    public void onResponse(
                            Call<PushTokenResponse> call,
                            Response<PushTokenResponse> response
                    ) {

                        if (response.isSuccessful()
                                && response.body() != null) {

                            PushTokenResponse result = response.body();

                            if (result.isSuccess()) {
                                Log.d(TAG, "Push token saved successfully");
                            } else {
                                Log.e(
                                        TAG,
                                        "Push token save failed: "
                                                + result.getMessage()
                                );
                            }

                        } else {
                            Log.e(
                                    TAG,
                                    "Push token API failed. Code: "
                                            + response.code()
                            );
                        }
                    }

                    @Override
                    public void onFailure(
                            Call<PushTokenResponse> call,
                            Throwable t
                    ) {

                        Log.e(
                                TAG,
                                "Push token request failed",
                                t
                        );
                    }
                });
    }
}
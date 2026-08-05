package com.akramhossain.bimtcharity.network;

import com.akramhossain.bimtcharity.models.LoginRequest;
import com.akramhossain.bimtcharity.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("ws/login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
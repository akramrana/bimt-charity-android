package com.akramhossain.bimtcharity.network;

import com.akramhossain.bimtcharity.models.DashboardResponse;
import com.akramhossain.bimtcharity.models.InvoiceResponse;
import com.akramhossain.bimtcharity.models.LoginRequest;
import com.akramhossain.bimtcharity.models.LoginResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @POST("ws/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @GET("ws/dashboard")
    Call<DashboardResponse> getDashboard(
            @Query("user_id") String userId
    );

    @GET("ws/monthly-invoice")
    Call<InvoiceResponse> getInvoices(
            @Query("user_id") String userId,
            @Query("page") int page
    );
}
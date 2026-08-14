package com.akramhossain.bimtcharity.network;

import com.akramhossain.bimtcharity.models.DashboardResponse;
import com.akramhossain.bimtcharity.models.FundRequestResponse;
import com.akramhossain.bimtcharity.models.InvoiceResponse;
import com.akramhossain.bimtcharity.models.LoginRequest;
import com.akramhossain.bimtcharity.models.LoginResponse;
import com.akramhossain.bimtcharity.models.PaymentReceivedResponse;
import com.akramhossain.bimtcharity.models.PaymentReleaseResponse;
import com.akramhossain.bimtcharity.models.UserResponse;

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

    @GET("ws/payment-received")
    Call<PaymentReceivedResponse> getPaymentsReceived(
            @Query("user_id") String userId,
            @Query("page") int page
    );

    @GET("ws/fund-request")
    Call<FundRequestResponse> getFundRequests(
            @Query("user_id") String userId,
            @Query("page") int page
    );

    @GET("ws/payment-release")
    Call<PaymentReleaseResponse> getPaymentReleases(
            @Query("user_id") String userId,
            @Query("page") int page
    );

    @GET("ws/user")
    Call<UserResponse> getUsers(
            @Query("user_id") String userId,
            @Query("page") int page
    );
}

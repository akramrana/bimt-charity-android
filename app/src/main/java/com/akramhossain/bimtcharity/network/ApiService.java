package com.akramhossain.bimtcharity.network;

import com.akramhossain.bimtcharity.models.DashboardResponse;
import com.akramhossain.bimtcharity.models.CmsResponse;
import com.akramhossain.bimtcharity.models.DeleteAccountRequest;
import com.akramhossain.bimtcharity.models.DeleteAccountResponse;
import com.akramhossain.bimtcharity.models.DocumentResponse;
import com.akramhossain.bimtcharity.models.EditProfileRequest;
import com.akramhossain.bimtcharity.models.EditProfileResponse;
import com.akramhossain.bimtcharity.models.FundRequestResponse;
import com.akramhossain.bimtcharity.models.InvoiceResponse;
import com.akramhossain.bimtcharity.models.LoginRequest;
import com.akramhossain.bimtcharity.models.LoginResponse;
import com.akramhossain.bimtcharity.models.NotificationResponse;
import com.akramhossain.bimtcharity.models.PaymentReceivedResponse;
import com.akramhossain.bimtcharity.models.PaymentReleaseResponse;
import com.akramhossain.bimtcharity.models.PaymentSubmitRequest;
import com.akramhossain.bimtcharity.models.PaymentSubmitResponse;
import com.akramhossain.bimtcharity.models.PushTokenRequest;
import com.akramhossain.bimtcharity.models.PushTokenResponse;
import com.akramhossain.bimtcharity.models.UnpaidInvoiceResponse;
import com.akramhossain.bimtcharity.models.UserResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @GET("ws/cms")
    Call<CmsResponse> getCmsPage(@Query("page") int pageId);

    @POST("ws/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("ws/edit-profile")
    Call<EditProfileResponse> editProfile(@Body EditProfileRequest request);

    @GET("ws/dashboard")
    Call<DashboardResponse> getDashboard(
            @Query("user_id") String userId
    );

    @GET("ws/monthly-invoice")
    Call<InvoiceResponse> getInvoices(
            @Query("user_id") String userId,
            @Query("page") int page,
            @Query("MonthlyInvoiceSearch[monthly_invoice_number]") String search
    );

    @GET("ws/payment-received")
    Call<PaymentReceivedResponse> getPaymentsReceived(
            @Query("user_id") String userId,
            @Query("page") int page,
            @Query("PaymentReceivedSearch[received_invoice_number]=") String search
    );

    @GET("ws/unpaid-invoices")
    Call<UnpaidInvoiceResponse> getUnpaidInvoices(
            @Query("user_id") String userId
    );

    @POST("ws/add-sadaqa")
    Call<PaymentSubmitResponse> submitPayment(
            @Body PaymentSubmitRequest request
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

    @GET("ws/document")
    Call<DocumentResponse> getDocuments(
            @Query("user_id") String userId,
            @Query("page") int page
    );

    @GET("ws/notification")
    Call<NotificationResponse> getNotifications(
            @Query("user_id") String userId,
            @Query("page") int page
    );

    @POST("ws/delete-account")
    Call<DeleteAccountResponse> deleteAccount(@Body DeleteAccountRequest request);

    @POST("ws/save-device-token")
    Call<PushTokenResponse> saveDeviceToken(
            @Body PushTokenRequest request
    );
}

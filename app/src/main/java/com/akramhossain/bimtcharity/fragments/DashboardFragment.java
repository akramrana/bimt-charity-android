package com.akramhossain.bimtcharity.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.akramhossain.bimtcharity.R;
import com.akramhossain.bimtcharity.adapters.DashboardCardAdapter;
import com.akramhossain.bimtcharity.adapters.FundStatusAdapter;
import com.akramhossain.bimtcharity.databinding.FragmentDashboardBinding;
import com.akramhossain.bimtcharity.models.DashboardCard;
import com.akramhossain.bimtcharity.models.DashboardResponse;
import com.akramhossain.bimtcharity.models.FundStatus;
import com.akramhossain.bimtcharity.network.ApiClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;
    private DashboardCardAdapter dashboardAdapter;
    private FundStatusAdapter fundStatusAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        setupRecyclerView();
        loadDashboard();

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        dashboardAdapter = new DashboardCardAdapter(
                new ArrayList<>(),
                card -> Toast.makeText(
                        requireContext(),
                        card.getTitle(),
                        Toast.LENGTH_SHORT
                ).show()
        );

        binding.dashboardRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.dashboardRecyclerView.setAdapter(dashboardAdapter);

        fundStatusAdapter = new FundStatusAdapter();

        binding.fundStatusRecyclerView.setLayoutManager(
                new GridLayoutManager(requireContext(), 2)
        );

        binding.fundStatusRecyclerView.setAdapter(fundStatusAdapter);
    }

    private void loadDashboard() {
        String userId = requireContext()
                .getSharedPreferences(
                        "bimt_session",
                        Context.MODE_PRIVATE
                )
                .getString("member_id", "");

        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(
                    requireContext(),
                    "Member information not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }

        showLoading();

        ApiClient.getApiService()
                .getDashboard(userId)
                .enqueue(new Callback<DashboardResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<DashboardResponse> call,
                            @NonNull Response<DashboardResponse> response
                    ) {

                        if (!isAdded() || binding == null) {
                            return;
                        }

                        hideLoading();

                        if (!response.isSuccessful()) {
                            Toast.makeText(
                                    requireContext(),
                                    "Dashboard request failed: HTTP "
                                            + response.code(),
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        DashboardResponse result = response.body();

                        if (result == null
                                || !result.isSuccess()
                                || result.getData() == null) {

                            String message = result == null
                                    ? "Invalid dashboard response"
                                    : result.getMessage();

                            if (message == null || message.trim().isEmpty()) {
                                message = "Unable to load dashboard";
                            }

                            Toast.makeText(
                                    requireContext(),
                                    message,
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        setupDashboardCards(result.getData());
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<DashboardResponse> call,
                            @NonNull Throwable throwable
                    ) {

                        if (!isAdded() || binding == null) {
                            return;
                        }

                        hideLoading();

                        Toast.makeText(
                                requireContext(),
                                "Connection failed: "
                                        + throwable.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    private void setupDashboardCards(DashboardResponse.DashboardData data) {
        List<DashboardCard> cards = new ArrayList<>();

        cards.add(new DashboardCard(
                R.drawable.ic_members,
                "NUMBER OF MEMBERS",
                safeValue(data.getUsers()),
                Color.parseColor("#F39C12")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_invoice,
                "INVOICE SENT THIS MONTH",
                safeValue(data.getMonthlyInvoice()),
                Color.parseColor("#00A65A")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_payment_received,
                "PAYMENT RECEIVED THIS MONTH",
                safeValue(data.getPaymentReceived()),
                Color.parseColor("#00C0EF")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_fund_request,
                "FUND REQUEST THIS MONTH",
                safeValue(data.getFundRequest()),
                Color.parseColor("#DD4B39")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_payment_release,
                "PAYMENT RELEASE THIS MONTH",
                safeValue(data.getPaymentRelease()),
                Color.parseColor("#00A65A")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_expense,
                "EXPENSE THIS MONTH",
                safeValue(data.getExpenses()),
                Color.parseColor("#00C0EF")
        ));

        dashboardAdapter.updateCards(cards);
        showFundStatuses(data.getStats());
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle("Dashboard");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private String safeValue(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "0";
        }

        return value;
    }

    private void showFundStatuses(
            List<DashboardResponse.FundStat> stats
    ) {
        List<FundStatus> items = new ArrayList<>();

        if (stats == null) {
            fundStatusAdapter.updateItems(items);
            return;
        }

        for (DashboardResponse.FundStat stat : stats) {
            String currency = "BDT";

            if (stat.getCurrencyStats() != null
                    && !stat.getCurrencyStats().isEmpty()
                    && stat.getCurrencyStats().get(0).getCode() != null) {

                currency = stat.getCurrencyStats().get(0).getCode();
            }

            items.add(new FundStatus(
                    safeValue(stat.getName()),
                    safeValue(stat.getFundRequestCount()),
                    safeValue(stat.getAmount()),
                    currency
            ));
        }

        fundStatusAdapter.updateItems(items);
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.dashboardContent.setVisibility(View.GONE);
    }

    private void hideLoading() {
        binding.progressBar.setVisibility(View.GONE);
        binding.dashboardContent.setVisibility(View.VISIBLE);
    }
}
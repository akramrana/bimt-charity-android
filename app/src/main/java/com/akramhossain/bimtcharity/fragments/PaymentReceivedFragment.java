package com.akramhossain.bimtcharity.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.adapters.PaymentReceivedAdapter;
import com.akramhossain.bimtcharity.databinding.FragmentPaymentReceivedBinding;
import com.akramhossain.bimtcharity.models.PaymentReceivedResponse;
import com.akramhossain.bimtcharity.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentReceivedFragment extends Fragment {

    private FragmentPaymentReceivedBinding binding;
    private PaymentReceivedAdapter adapter;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentPaymentReceivedBinding.inflate(
                inflater,
                container,
                false
        );

        setupRecyclerView();
        binding.fabSubmitProof.setOnClickListener(view ->
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(com.akramhossain.bimtcharity.R.id.contentFrame,
                                new SubmitPaymentFragment())
                        .addToBackStack(null)
                        .commit()
        );
        loadPaymentsReceived(1);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new PaymentReceivedAdapter();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext());

        binding.paymentReceivedRecyclerView.setLayoutManager(layoutManager);
        binding.paymentReceivedRecyclerView.setAdapter(adapter);

        binding.paymentReceivedRecyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(
                            @NonNull RecyclerView recyclerView,
                            int dx,
                            int dy
                    ) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (dy <= 0 || isLoading || !hasMorePages) {
                            return;
                        }

                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int firstVisibleItemPosition =
                                layoutManager.findFirstVisibleItemPosition();

                        if (visibleItemCount + firstVisibleItemPosition
                                >= totalItemCount - 3) {
                            loadNextPage();
                        }
                    }
                }
        );
    }

    private void loadNextPage() {
        if (isLoading || !hasMorePages) {
            return;
        }

        loadPaymentsReceived(currentPage + 1);
    }

    private void loadPaymentsReceived(int page) {
        if (isLoading || !hasMorePages) {
            return;
        }

        String userId = requireContext()
                .getSharedPreferences(
                        "bimt_session",
                        Context.MODE_PRIVATE
                )
                .getString("member_id", "");

        if (userId == null || userId.isEmpty()) {
            binding.txtEmpty.setText("Member information not found");
            binding.txtEmpty.setVisibility(View.VISIBLE);
            return;
        }

        isLoading = true;

        if (page == 1) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.txtEmpty.setVisibility(View.GONE);
        }

        ApiClient.getApiService()
                .getPaymentsReceived(userId, page)
                .enqueue(new Callback<PaymentReceivedResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<PaymentReceivedResponse> call,
                            @NonNull Response<PaymentReceivedResponse> response
                    ) {
                        if (binding == null) {
                            return;
                        }

                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);

                        PaymentReceivedResponse result = response.body();

                        if (!response.isSuccessful()
                                || result == null
                                || !result.isSuccess()
                                || result.getData() == null) {
                            Toast.makeText(
                                    requireContext(),
                                    "Unable to load payments received",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        List<PaymentReceivedResponse.PaymentReceived> payments =
                                result.getData().getDataProvider();

                        PaymentReceivedResponse.Pagination pagination =
                                result.getData().getPagination();

                        if (payments == null || payments.isEmpty()) {
                            hasMorePages = false;

                            if (page == 1) {
                                binding.txtEmpty.setText(
                                        "No payments received found"
                                );
                                binding.txtEmpty.setVisibility(View.VISIBLE);
                            }
                            return;
                        }

                        if (page == 1) {
                            adapter.setPayments(payments);
                        } else {
                            adapter.addPayments(payments);
                        }

                        if (pagination != null) {
                            currentPage = pagination.getPage();
                            hasMorePages = pagination.getPage()
                                    < pagination.getPageCount();
                        } else {
                            currentPage = page;
                            hasMorePages = !payments.isEmpty();
                        }

                        binding.txtEmpty.setVisibility(
                                adapter.getItemCount() == 0
                                        ? View.VISIBLE
                                        : View.GONE
                        );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<PaymentReceivedResponse> call,
                            @NonNull Throwable throwable
                    ) {
                        if (binding == null) {
                            return;
                        }

                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                requireContext(),
                                "Connection failed: " + throwable.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

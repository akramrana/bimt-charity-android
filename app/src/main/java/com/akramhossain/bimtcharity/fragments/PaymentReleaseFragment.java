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

import com.akramhossain.bimtcharity.adapters.PaymentReleaseAdapter;
import com.akramhossain.bimtcharity.databinding.FragmentPaymentReleaseBinding;
import com.akramhossain.bimtcharity.models.PaymentReleaseResponse;
import com.akramhossain.bimtcharity.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentReleaseFragment extends Fragment {

    private FragmentPaymentReleaseBinding binding;
    private PaymentReleaseAdapter adapter;

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
        binding = FragmentPaymentReleaseBinding.inflate(
                inflater,
                container,
                false
        );

        setupRecyclerView();
        loadPaymentReleases(1);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new PaymentReleaseAdapter();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext());

        binding.paymentReleaseRecyclerView.setLayoutManager(layoutManager);
        binding.paymentReleaseRecyclerView.setAdapter(adapter);

        binding.paymentReleaseRecyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(
                            @NonNull RecyclerView recyclerView,
                            int dx,
                            int dy
                    ) {
                        super.onScrolled(recyclerView, dx, dy);

                        if (dy <= 0) {
                            return;
                        }

                        if (isLoading || !hasMorePages) {
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

        loadPaymentReleases(currentPage + 1);
    }

    private void loadPaymentReleases(int page) {
        if (isLoading || !hasMorePages) {
            return;
        }

        String userId = requireContext()
                .getSharedPreferences("bimt_session", Context.MODE_PRIVATE)
                .getString("member_id", "");

        if (userId == null || userId.isEmpty()) {
            return;
        }

        isLoading = true;

        if (page == 1) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.txtEmpty.setVisibility(View.GONE);
        }

        ApiClient.getApiService()
                .getPaymentReleases(userId, page)
                .enqueue(new Callback<PaymentReleaseResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<PaymentReleaseResponse> call,
                            @NonNull Response<PaymentReleaseResponse> response
                    ) {
                        if (binding == null) {
                            return;
                        }

                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);

                        PaymentReleaseResponse result = response.body();

                        if (!response.isSuccessful()
                                || result == null
                                || !result.isSuccess()
                                || result.getData() == null) {
                            Toast.makeText(
                                    requireContext(),
                                    "Unable to load payment releases",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        List<PaymentReleaseResponse.PaymentRelease> releases =
                                result.getData().getDataProvider();
                        PaymentReleaseResponse.Pagination pagination =
                                result.getData().getPagination();

                        if (releases == null || releases.isEmpty()) {
                            hasMorePages = false;

                            if (page == 1) {
                                binding.txtEmpty.setVisibility(View.VISIBLE);
                            }
                            return;
                        }

                        if (page == 1) {
                            adapter.setPaymentReleases(releases);
                        } else {
                            adapter.addPaymentReleases(releases);
                        }

                        if (pagination != null) {
                            currentPage = pagination.getPage();
                            hasMorePages = pagination.getPage()
                                    < pagination.getPageCount();
                        } else {
                            currentPage = page;
                            hasMorePages = !releases.isEmpty();
                        }

                        binding.txtEmpty.setVisibility(
                                adapter.getItemCount() == 0
                                        ? View.VISIBLE
                                        : View.GONE
                        );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<PaymentReleaseResponse> call,
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

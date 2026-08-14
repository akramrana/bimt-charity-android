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

import com.akramhossain.bimtcharity.adapters.FundRequestAdapter;
import com.akramhossain.bimtcharity.databinding.FragmentFundRequestBinding;
import com.akramhossain.bimtcharity.models.FundRequestResponse;
import com.akramhossain.bimtcharity.network.ApiClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FundRequestFragment extends Fragment {

    private FragmentFundRequestBinding binding;
    private FundRequestAdapter adapter;

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
        binding = FragmentFundRequestBinding.inflate(inflater, container, false);

        setupRecyclerView();
        loadFundRequests(1);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new FundRequestAdapter();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext());

        binding.fundRequestRecyclerView.setLayoutManager(layoutManager);
        binding.fundRequestRecyclerView.setAdapter(adapter);

        binding.fundRequestRecyclerView.addOnScrollListener(
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

        loadFundRequests(currentPage + 1);
    }

    private void loadFundRequests(int page) {
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
                .getFundRequests(userId, page)
                .enqueue(new Callback<FundRequestResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<FundRequestResponse> call,
                            @NonNull Response<FundRequestResponse> response
                    ) {
                        if (binding == null) {
                            return;
                        }

                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);

                        FundRequestResponse result = response.body();

                        if (!response.isSuccessful()
                                || result == null
                                || !result.isSuccess()
                                || result.getData() == null) {
                            Toast.makeText(
                                    requireContext(),
                                    "Unable to load fund requests",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        List<FundRequestResponse.FundRequest> fundRequests =
                                result.getData().getDataProvider();
                        FundRequestResponse.Pagination pagination =
                                result.getData().getPagination();

                        if (fundRequests == null || fundRequests.isEmpty()) {
                            hasMorePages = false;

                            if (page == 1) {
                                binding.txtEmpty.setVisibility(View.VISIBLE);
                            }
                            return;
                        }

                        if (page == 1) {
                            adapter.setFundRequests(fundRequests);
                        } else {
                            adapter.addFundRequests(fundRequests);
                        }

                        if (pagination != null) {
                            currentPage = pagination.getPage();
                            hasMorePages = pagination.getPage()
                                    < pagination.getPageCount();
                        } else {
                            currentPage = page;
                            hasMorePages = !fundRequests.isEmpty();
                        }

                        binding.txtEmpty.setVisibility(
                                adapter.getItemCount() == 0
                                        ? View.VISIBLE
                                        : View.GONE
                        );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<FundRequestResponse> call,
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

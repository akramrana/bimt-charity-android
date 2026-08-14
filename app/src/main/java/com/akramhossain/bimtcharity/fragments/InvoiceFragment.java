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

import com.akramhossain.bimtcharity.adapters.InvoiceAdapter;
import com.akramhossain.bimtcharity.databinding.FragmentInvoiceBinding;
import com.akramhossain.bimtcharity.models.InvoiceResponse;
import com.akramhossain.bimtcharity.network.ApiClient;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InvoiceFragment extends Fragment {

    private FragmentInvoiceBinding binding;
    private InvoiceAdapter adapter;

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
        binding = FragmentInvoiceBinding.inflate(
                inflater,
                container,
                false
        );

        setupRecyclerView();
        loadInvoices(1);

        return binding.getRoot();
    }

    private void setupRecyclerView() {

        adapter = new InvoiceAdapter();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext());

        binding.invoiceRecyclerView.setLayoutManager(layoutManager);
        binding.invoiceRecyclerView.setAdapter(adapter);

        binding.invoiceRecyclerView.addOnScrollListener(
                new RecyclerView.OnScrollListener() {

                    @Override
                    public void onScrolled(
                            @NonNull RecyclerView recyclerView,
                            int dx,
                            int dy
                    ) {
                        super.onScrolled(recyclerView, dx, dy);

                        // Only react when scrolling downward
                        if (dy <= 0) {
                            return;
                        }

                        if (isLoading || !hasMorePages) {
                            return;
                        }

                        int visibleItemCount =
                                layoutManager.getChildCount();

                        int totalItemCount =
                                layoutManager.getItemCount();

                        int firstVisibleItemPosition =
                                layoutManager.findFirstVisibleItemPosition();

                        // Start loading before reaching the absolute bottom
                        if (visibleItemCount
                                + firstVisibleItemPosition
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

        loadInvoices(currentPage + 1);
    }

    private void loadInvoices(int page) {

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
            return;
        }

        isLoading = true;

        if (page == 1) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.txtEmpty.setVisibility(View.GONE);
        }

        ApiClient.getApiService()
                .getInvoices(userId, page)
                .enqueue(new Callback<InvoiceResponse>() {

                    @Override
                    public void onResponse(
                            @NonNull Call<InvoiceResponse> call,
                            @NonNull Response<InvoiceResponse> response
                    ) {
                        if (binding == null) {
                            return;
                        }

                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);

                        InvoiceResponse result = response.body();

                        if (!response.isSuccessful()
                                || result == null
                                || !result.isSuccess()
                                || result.getData() == null) {

                            Toast.makeText(
                                    requireContext(),
                                    "Unable to load invoices",
                                    Toast.LENGTH_LONG
                            ).show();

                            return;
                        }

                        List<InvoiceResponse.Invoice> invoices =
                                result.getData().getDataProvider();

                        InvoiceResponse.Pagination pagination =
                                result.getData().getPagination();

                        if (invoices == null || invoices.isEmpty()) {

                            hasMorePages = false;

                            if (page == 1) {
                                binding.txtEmpty.setVisibility(View.VISIBLE);
                            }

                            return;
                        }

                        if (page == 1) {
                            adapter.setInvoices(invoices);
                        } else {
                            adapter.addInvoices(invoices);
                        }

                        if (pagination != null) {

                            currentPage = pagination.getPage();

                            hasMorePages =
                                    pagination.getPage() < pagination.getPageCount();

                        } else {

                            currentPage = page;

                            hasMorePages = !invoices.isEmpty();
                        }

                        binding.txtEmpty.setVisibility(
                                adapter.getItemCount() == 0
                                        ? View.VISIBLE
                                        : View.GONE
                        );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<InvoiceResponse> call,
                            @NonNull Throwable throwable
                    ) {
                        if (binding == null) {
                            return;
                        }

                        isLoading = false;

                        binding.progressBar.setVisibility(View.GONE);

                        Toast.makeText(
                                requireContext(),
                                "Connection failed: "
                                        + throwable.getMessage(),
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
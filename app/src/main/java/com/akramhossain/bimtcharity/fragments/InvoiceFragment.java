package com.akramhossain.bimtcharity.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.akramhossain.bimtcharity.R;
import com.akramhossain.bimtcharity.adapters.InvoiceAdapter;
import com.akramhossain.bimtcharity.databinding.FragmentInvoiceBinding;
import com.akramhossain.bimtcharity.models.InvoiceResponse;
import com.akramhossain.bimtcharity.network.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;
import androidx.appcompat.widget.Toolbar;

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
    private String currentSearch = "";
    private final Handler searchHandler = new Handler(Looper.getMainLooper());

    private Runnable searchRunnable;

    private EditText searchEditText;

    private Call<InvoiceResponse> invoiceCall;

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

        String search = currentSearch.isEmpty()
                ? null
                : currentSearch;

        invoiceCall = ApiClient.getApiService().getInvoices(userId, page, search);

        invoiceCall.enqueue(new Callback<InvoiceResponse>() {

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

    public void showSearch() {
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.toolbar);

        if (searchEditText != null) {
            searchEditText.requestFocus();
            return;
        }

        toolbar.setTitle("");

        searchEditText = new EditText(requireContext());

        searchEditText.setHint("Search invoices...");
        searchEditText.setSingleLine(true);

        TypedValue typedValue = new TypedValue();
        requireContext().getTheme().resolveAttribute(
                com.google.android.material.R.attr.colorOnSurface,
                typedValue,
                true
        );

        searchEditText.setTextColor(typedValue.data);

        requireContext().getTheme().resolveAttribute(
                com.google.android.material.R.attr.colorOnSurfaceVariant,
                typedValue,
                true
        );
        searchEditText.setHintTextColor(typedValue.data);

        searchEditText.setBackgroundColor(Color.TRANSPARENT);

        Toolbar.LayoutParams params = new Toolbar.LayoutParams(
                Toolbar.LayoutParams.MATCH_PARENT,
                Toolbar.LayoutParams.MATCH_PARENT
        );

        params.setMarginEnd(80);

        searchEditText.setLayoutParams(params);

        toolbar.addView(searchEditText);

        searchEditText.requestFocus();

        InputMethodManager imm =
                (InputMethodManager) requireContext()
                        .getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.showSoftInput(
                searchEditText,
                InputMethodManager.SHOW_IMPLICIT
        );

        searchEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(
                    CharSequence s,
                    int start,
                    int count,
                    int after
            ) {}

            @Override
            public void onTextChanged(
                    CharSequence s,
                    int start,
                    int before,
                    int count
            ) {

                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                String query = s.toString().trim();

                searchRunnable = () -> searchInvoices(query);

                searchHandler.postDelayed(searchRunnable, 500);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void searchInvoices(String query) {

        currentSearch = query.trim();

        currentPage = 1;
        hasMorePages = true;

        loadInvoices(1);
    }

    @Override
    public void onDestroyView() {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        if (invoiceCall != null) {
            invoiceCall.cancel();
        }

        if (searchEditText != null) {
            MaterialToolbar toolbar = requireActivity().findViewById(R.id.toolbar);
            toolbar.removeView(searchEditText);
            searchEditText = null;
        }
        binding = null;
        super.onDestroyView();

    }
}
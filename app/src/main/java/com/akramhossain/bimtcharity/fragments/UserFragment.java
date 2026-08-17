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
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.R;
import com.akramhossain.bimtcharity.adapters.UserAdapter;
import com.akramhossain.bimtcharity.databinding.FragmentUserBinding;
import com.akramhossain.bimtcharity.models.UserResponse;
import com.akramhossain.bimtcharity.network.ApiClient;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserFragment extends Fragment {

    private FragmentUserBinding binding;
    private UserAdapter adapter;

    private int currentPage = 1;
    private boolean isLoading = false;
    private boolean hasMorePages = true;

    private String currentSearch = "";
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;
    private EditText searchEditText;
    private Call<UserResponse> apiCall;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentUserBinding.inflate(inflater, container, false);

        setupRecyclerView();
        loadUsers(1);

        return binding.getRoot();
    }

    private void setupRecyclerView() {
        adapter = new UserAdapter();

        LinearLayoutManager layoutManager =
                new LinearLayoutManager(requireContext());

        binding.userRecyclerView.setLayoutManager(layoutManager);
        binding.userRecyclerView.setAdapter(adapter);

        binding.userRecyclerView.addOnScrollListener(
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

        loadUsers(currentPage + 1);
    }

    private void loadUsers(int page) {
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

        String search = currentSearch.isEmpty()
                ? null
                : currentSearch;

        apiCall = ApiClient.getApiService()
                .getUsers(userId, page, search);
        apiCall.enqueue(new Callback<UserResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<UserResponse> call,
                            @NonNull Response<UserResponse> response
                    ) {
                        if (binding == null) {
                            return;
                        }

                        isLoading = false;
                        binding.progressBar.setVisibility(View.GONE);

                        UserResponse result = response.body();

                        if (!response.isSuccessful()
                                || result == null
                                || !result.isSuccess()
                                || result.getData() == null) {
                            Toast.makeText(
                                    requireContext(),
                                    "Unable to load users",
                                    Toast.LENGTH_LONG
                            ).show();
                            return;
                        }

                        List<UserResponse.User> users =
                                result.getData().getDataProvider();
                        UserResponse.Pagination pagination =
                                result.getData().getPagination();

                        if (users == null || users.isEmpty()) {
                            hasMorePages = false;

                            if (page == 1) {
                                binding.txtEmpty.setVisibility(View.VISIBLE);
                            }
                            return;
                        }

                        if (page == 1) {
                            adapter.setUsers(users);
                        } else {
                            adapter.addUsers(users);
                        }

                        if (pagination != null) {
                            currentPage = pagination.getPage();
                            hasMorePages = pagination.getPage()
                                    < pagination.getPageCount();
                        } else {
                            currentPage = page;
                            hasMorePages = !users.isEmpty();
                        }

                        binding.txtEmpty.setVisibility(
                                adapter.getItemCount() == 0
                                        ? View.VISIBLE
                                        : View.GONE
                        );
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<UserResponse> call,
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

    private void searchInvoices(String query) {

        currentSearch = query.trim();

        currentPage = 1;
        hasMorePages = true;

        loadUsers(1);
    }

    @Override
    public void onDestroyView() {
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }

        if (apiCall != null) {
            apiCall.cancel();
        }

        if (searchEditText != null) {
            MaterialToolbar toolbar = requireActivity().findViewById(R.id.toolbar);
            toolbar.removeView(searchEditText);
            searchEditText = null;
        }
        binding = null;
        super.onDestroyView();
    }

    public void showSearch() {
        MaterialToolbar toolbar = requireActivity().findViewById(R.id.toolbar);

        if (searchEditText != null) {
            searchEditText.requestFocus();
            return;
        }

        toolbar.setTitle("");

        searchEditText = new EditText(requireContext());

        searchEditText.setHint("Search members ...");
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
}

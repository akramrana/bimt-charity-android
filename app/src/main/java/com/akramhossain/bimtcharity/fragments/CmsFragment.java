package com.akramhossain.bimtcharity.fragments;

import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akramhossain.bimtcharity.databinding.FragmentCmsBinding;
import com.akramhossain.bimtcharity.models.CmsResponse;
import com.akramhossain.bimtcharity.network.ApiClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CmsFragment extends Fragment {

    public static final String ARG_PAGE_ID = "ARG_PAGE_ID";
    public static final String ARG_TITLE = "ARG_TITLE";

    private static final String ERROR_MESSAGE =
            "Unable to load content. Please try again.";

    private FragmentCmsBinding binding;
    private Call<CmsResponse> cmsCall;

    public static CmsFragment newInstance(int pageId, String title) {
        CmsFragment fragment = new CmsFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARG_PAGE_ID, pageId);
        arguments.putString(ARG_TITLE, title);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentCmsBinding.inflate(inflater, container, false);

        Bundle arguments = getArguments();
        int pageId = arguments == null ? 0 : arguments.getInt(ARG_PAGE_ID, 0);
        String title = arguments == null ? null : arguments.getString(ARG_TITLE);

        if (!TextUtils.isEmpty(title)) {
            requireActivity().setTitle(title);
        }

        binding.txtCmsContent.setMovementMethod(LinkMovementMethod.getInstance());

        if (pageId <= 0) {
            showError();
        } else {
            loadCmsPage(pageId);
        }

        return binding.getRoot();
    }

    private void loadCmsPage(int pageId) {
        showLoading();

        cmsCall = ApiClient.getApiService().getCmsPage(pageId);
        cmsCall.enqueue(new Callback<CmsResponse>() {
            @Override
            public void onResponse(
                    @NonNull Call<CmsResponse> call,
                    @NonNull Response<CmsResponse> response
            ) {
                if (binding == null) {
                    return;
                }

                CmsResponse result = response.body();
                CmsResponse.CmsData data = result == null ? null : result.getData();
                String content = data == null ? null : data.getContent();

                if (!response.isSuccessful()
                        || result == null
                        || !result.isSuccess()
                        || data == null
                        || TextUtils.isEmpty(content)
                        || TextUtils.isEmpty(Html.fromHtml(
                                content,
                                Html.FROM_HTML_MODE_LEGACY
                        ).toString().trim())) {
                    showError();
                    return;
                }

                if (!TextUtils.isEmpty(data.getTitle()) && isAdded()) {
                    requireActivity().setTitle(data.getTitle());
                }

                binding.txtCmsContent.setText(Html.fromHtml(
                        content,
                        Html.FROM_HTML_MODE_LEGACY
                ));
                showContent();
            }

            @Override
            public void onFailure(
                    @NonNull Call<CmsResponse> call,
                    @NonNull Throwable throwable
            ) {
                if (binding == null || call.isCanceled()) {
                    return;
                }
                showError();
            }
        });
    }

    private void showLoading() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.cmsScrollView.setVisibility(View.GONE);
        binding.txtError.setVisibility(View.GONE);
    }

    private void showContent() {
        binding.progressBar.setVisibility(View.GONE);
        binding.cmsScrollView.setVisibility(View.VISIBLE);
        binding.txtError.setVisibility(View.GONE);
    }

    private void showError() {
        binding.progressBar.setVisibility(View.GONE);
        binding.cmsScrollView.setVisibility(View.GONE);
        binding.txtError.setText(ERROR_MESSAGE);
        binding.txtError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        if (cmsCall != null) {
            cmsCall.cancel();
            cmsCall = null;
        }
        binding = null;
        super.onDestroyView();
    }
}

package com.akramhossain.bimtcharity.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.R;
import com.akramhossain.bimtcharity.databinding.ItemFundRequestBinding;
import com.akramhossain.bimtcharity.models.FundRequestResponse;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FundRequestAdapter
        extends RecyclerView.Adapter<FundRequestAdapter.FundRequestViewHolder> {

    private final List<FundRequestResponse.FundRequest> fundRequests =
            new ArrayList<>();

    public void setFundRequests(List<FundRequestResponse.FundRequest> items) {
        fundRequests.clear();

        if (items != null) {
            fundRequests.addAll(items);
        }

        notifyDataSetChanged();
    }

    public void addFundRequests(List<FundRequestResponse.FundRequest> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int startPosition = fundRequests.size();
        fundRequests.addAll(items);
        notifyItemRangeInserted(startPosition, items.size());
    }

    @NonNull
    @Override
    public FundRequestViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemFundRequestBinding binding = ItemFundRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new FundRequestViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull FundRequestViewHolder holder,
            int position
    ) {
        holder.bind(fundRequests.get(position));
    }

    @Override
    public int getItemCount() {
        return fundRequests.size();
    }

    static class FundRequestViewHolder extends RecyclerView.ViewHolder {

        private final ItemFundRequestBinding binding;

        FundRequestViewHolder(ItemFundRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FundRequestResponse.FundRequest request) {
            binding.txtRequestNumber.setText(
                    "Fund request #" + safeText(
                            request.getFundRequestNumber(),
                            String.valueOf(request.getFundRequestId())
                    )
            );

            binding.txtTitle.setText(safeText(request.getTitle(), "Untitled request"));

            binding.txtRequestUser.setText(
                    safeText(request.getRequestUser(), "Not set")
            );

            String description = safeText(
                    request.getRequestDescription(),
                    "Not set"
            );

            View.OnClickListener showDescription = v ->
                    new MaterialAlertDialogBuilder(v.getContext())
                            .setTitle("Fund request details")
                            .setMessage(description)
                            .setPositiveButton("Close", null)
                            .show();

            binding.txtDescription.setText(
                    safeText(description, "No description")
            );

            binding.txtDescription.setOnClickListener(showDescription);
            binding.txtReadMore.setOnClickListener(showDescription);

            binding.txtReason.setText(
                    "Reason: " + safeText(request.getReason(), "Not set")
            );
            binding.txtCreatedAt.setText(
                    "Created: " + safeText(request.getCreatedAt(), "Not set")
            );

            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            binding.txtAmount.setText(
                    safeText(request.getCurrencyCode(), "")
                            + " "
                            + formatter.format(request.getRequestAmount())
            );

            String status = safeText(request.getApprovalStatus(), "Pending");
            binding.txtApprovalStatus.setText(status.toUpperCase(Locale.US));

            if ("approved".equalsIgnoreCase(status)) {
                binding.txtApprovalStatus.setTextColor(Color.parseColor("#1B5E20"));
                binding.txtApprovalStatus.setBackgroundResource(
                        R.drawable.bg_status_paid
                );
            } else {
                binding.txtApprovalStatus.setTextColor(Color.parseColor("#B71C1C"));
                binding.txtApprovalStatus.setBackgroundResource(
                        R.drawable.bg_status_unpaid
                );
            }
        }

        private String safeText(String value, String fallback) {
            return value == null || value.trim().isEmpty() ? fallback : value;
        }
    }
}

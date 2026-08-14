package com.akramhossain.bimtcharity.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.databinding.ItemPaymentReleaseBinding;
import com.akramhossain.bimtcharity.models.PaymentReleaseResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentReleaseAdapter
        extends RecyclerView.Adapter<PaymentReleaseAdapter.PaymentReleaseViewHolder> {

    private final List<PaymentReleaseResponse.PaymentRelease> paymentReleases =
            new ArrayList<>();

    public void setPaymentReleases(
            List<PaymentReleaseResponse.PaymentRelease> items
    ) {
        paymentReleases.clear();

        if (items != null) {
            paymentReleases.addAll(items);
        }

        notifyDataSetChanged();
    }

    public void addPaymentReleases(
            List<PaymentReleaseResponse.PaymentRelease> items
    ) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int startPosition = paymentReleases.size();
        paymentReleases.addAll(items);
        notifyItemRangeInserted(startPosition, items.size());
    }

    @NonNull
    @Override
    public PaymentReleaseViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemPaymentReleaseBinding binding =
                ItemPaymentReleaseBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new PaymentReleaseViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PaymentReleaseViewHolder holder,
            int position
    ) {
        holder.bind(paymentReleases.get(position));
    }

    @Override
    public int getItemCount() {
        return paymentReleases.size();
    }

    static class PaymentReleaseViewHolder extends RecyclerView.ViewHolder {

        private final ItemPaymentReleaseBinding binding;

        PaymentReleaseViewHolder(ItemPaymentReleaseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PaymentReleaseResponse.PaymentRelease paymentRelease) {
            binding.txtReleaseInvoiceNumber.setText(
                    "Payment release #" + safeText(
                            paymentRelease.getReleaseInvoiceNumber(),
                            String.valueOf(paymentRelease.getPaymentReleaseId())
                    )
            );

            binding.txtFundRequest.setText(
                    "Fund request: " + safeText(
                            paymentRelease.getFundRequest(),
                            String.valueOf(paymentRelease.getFundRequestId())
                    )
            );


            binding.txtReleasedBy.setText(
                    safeText(paymentRelease.getReleaseBy(), "Not set")
            );
            binding.txtNote.setText(
                    "Note: " + safeText(paymentRelease.getNote(), "Not set")
            );
            binding.txtCreatedAt.setText(
                    "Created: "
                            + safeText(paymentRelease.getCreatedAt(), "Not set")
            );

            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            binding.txtAmount.setText(
                    safeText(paymentRelease.getCurrencyCode(), "")
                            + " "
                            + formatter.format(paymentRelease.getAmount())
            );
        }

        private String safeText(String value, String fallback) {
            return value == null || value.trim().isEmpty() ? fallback : value;
        }
    }
}

package com.akramhossain.bimtcharity.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.R;
import com.akramhossain.bimtcharity.databinding.ItemPaymentReceivedBinding;
import com.akramhossain.bimtcharity.models.PaymentReceivedResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PaymentReceivedAdapter
        extends RecyclerView.Adapter<PaymentReceivedAdapter.PaymentReceivedViewHolder> {

    private final List<PaymentReceivedResponse.PaymentReceived> payments =
            new ArrayList<>();

    public void setPayments(List<PaymentReceivedResponse.PaymentReceived> items) {
        payments.clear();

        if (items != null) {
            payments.addAll(items);
        }

        notifyDataSetChanged();
    }

    public void addPayments(List<PaymentReceivedResponse.PaymentReceived> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int startPosition = payments.size();

        payments.addAll(items);

        notifyItemRangeInserted(startPosition, items.size());
    }

    @NonNull
    @Override
    public PaymentReceivedViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemPaymentReceivedBinding binding =
                ItemPaymentReceivedBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new PaymentReceivedViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull PaymentReceivedViewHolder holder,
            int position
    ) {
        holder.bind(payments.get(position));
    }

    @Override
    public int getItemCount() {
        return payments.size();
    }

    static class PaymentReceivedViewHolder extends RecyclerView.ViewHolder {

        private final ItemPaymentReceivedBinding binding;

        PaymentReceivedViewHolder(ItemPaymentReceivedBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PaymentReceivedResponse.PaymentReceived payment) {

            binding.txtReceiptNumber.setText(
                    "Sadaqah invoice #" + safeText(
                            payment.getReceivedInvoiceNumber(),
                            "N/A"
                    )
            );

            binding.txtInstalmentPeriod.setText(
                    safeText(payment.getInstalmentMonth(), "")
                            + " "
                            + payment.getInstalmentYear()
            );

            binding.txtDonatedBy.setText(
                    safeText(payment.getDonatedBy(), "Not set")
            );

            binding.txtReceivedBy.setText(
                    safeText(payment.getReceiverName(), "Not set")
            );

            binding.txtComments.setText(
                    safeText(payment.getComments(), "Not set")
            );

            binding.txtProof.setText(
                    payment.getFile() == null
                            || payment.getFile().trim().isEmpty()
                            ? "Not set"
                            : "Available"
            );

            String monthlyInvoiceNumber = payment.getMonthlyInvoiceNumber();

            if (monthlyInvoiceNumber == null
                    || monthlyInvoiceNumber.trim().isEmpty()) {

                monthlyInvoiceNumber = payment.getMonthlyInvoiceId() > 0
                        ? String.valueOf(payment.getMonthlyInvoiceId())
                        : "Not set";
            }

            if (!monthlyInvoiceNumber.equals("Not set")) {
                monthlyInvoiceNumber = "#" + monthlyInvoiceNumber;
            }

            binding.txtMonthlyInvoiceNumber.setText(monthlyInvoiceNumber);

            NumberFormat formatter =
                    NumberFormat.getNumberInstance(Locale.US);

            binding.txtAmount.setText(
                    safeText(payment.getCurrencyCode(), "")
                            + " "
                            + formatter.format(payment.getAmount())
            );

            if (payment.getHasInvoice() == 1) {

                binding.txtInvoiceStatus.setText("HAS INVOICE");
                binding.txtInvoiceStatus.setTextColor(
                        Color.parseColor("#1B5E20")
                );
                binding.txtInvoiceStatus.setBackgroundResource(
                        R.drawable.bg_status_paid
                );

            } else {

                binding.txtInvoiceStatus.setText("NO INVOICE");
                binding.txtInvoiceStatus.setTextColor(
                        Color.parseColor("#B71C1C")
                );
                binding.txtInvoiceStatus.setBackgroundResource(
                        R.drawable.bg_status_unpaid
                );
            }
        }

        private String safeText(String value, String fallback) {
            return value == null || value.trim().isEmpty() ? fallback : value;
        }
    }
}

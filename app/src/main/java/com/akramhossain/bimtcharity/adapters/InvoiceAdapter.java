package com.akramhossain.bimtcharity.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.databinding.ItemInvoiceBinding;
import com.akramhossain.bimtcharity.models.InvoiceResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class InvoiceAdapter
        extends RecyclerView.Adapter<InvoiceAdapter.InvoiceViewHolder> {

    private final List<InvoiceResponse.Invoice> invoices = new ArrayList<>();

    public void setInvoices(List<InvoiceResponse.Invoice> items) {
        invoices.clear();

        if (items != null) {
            invoices.addAll(items);
        }

        notifyDataSetChanged();
    }

    public void addInvoices(List<InvoiceResponse.Invoice> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int startPosition = invoices.size();

        invoices.addAll(items);

        notifyItemRangeInserted(startPosition, items.size());
    }

    @NonNull
    @Override
    public InvoiceViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemInvoiceBinding binding =
                ItemInvoiceBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new InvoiceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull InvoiceViewHolder holder,
            int position
    ) {
        holder.bind(invoices.get(position));
    }

    @Override
    public int getItemCount() {
        return invoices.size();
    }

    static class InvoiceViewHolder extends RecyclerView.ViewHolder {

        private final ItemInvoiceBinding binding;

        InvoiceViewHolder(ItemInvoiceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(InvoiceResponse.Invoice invoice) {

            binding.txtInvoiceNumber.setText(
                    "Invoice #" + invoice.getMonthlyInvoiceNumber()
            );

            binding.txtMonth.setText(
                    invoice.getInstalmentMonth()
                            + " "
                            + invoice.getInstalmentYear()
            );

            binding.txtReceiver.setText(invoice.getReceiverName());

            NumberFormat formatter =
                    NumberFormat.getNumberInstance(Locale.US);

            binding.txtAmount.setText(
                    invoice.getCurrencyCode()+" " + formatter.format(invoice.getAmount())
            );

            binding.txtStatus.setText(
                    invoice.getIsPaid() == 1
                            ? "PAID"
                            : "UNPAID"
            );
        }
    }
}
package com.akramhossain.bimtcharity.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.databinding.ItemFundStatusBinding;
import com.akramhossain.bimtcharity.models.FundStatus;

import java.util.ArrayList;
import java.util.List;

public class FundStatusAdapter
        extends RecyclerView.Adapter<FundStatusAdapter.StatusViewHolder> {

    private final List<FundStatus> items = new ArrayList<>();

    public void updateItems(List<FundStatus> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StatusViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemFundStatusBinding binding =
                ItemFundStatusBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new StatusViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull StatusViewHolder holder,
            int position
    ) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class StatusViewHolder extends RecyclerView.ViewHolder {

        private final ItemFundStatusBinding binding;

        StatusViewHolder(ItemFundStatusBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FundStatus item) {
            binding.txtStatusName.setText(item.getName());

            String requestLabel = "1".equals(item.getCount())
                    ? " request"
                    : " requests";

            binding.txtStatusCount.setText(
                    item.getCount() + requestLabel
            );

            if ("0".equals(item.getAmount())) {
                binding.txtStatusAmount.setText("No amount");
            } else {
                binding.txtStatusAmount.setText(
                        item.getCurrency() + " " + formatAmount(item.getAmount())
                );
            }
        }

        private String formatAmount(String amount) {
            try {
                long value = Long.parseLong(amount);
                return String.format("%,d", value);
            } catch (NumberFormatException exception) {
                return amount;
            }
        }
    }
}
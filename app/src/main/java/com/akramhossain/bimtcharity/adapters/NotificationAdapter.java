package com.akramhossain.bimtcharity.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.databinding.ItemNotificationBinding;
import com.akramhossain.bimtcharity.models.NotificationResponse;

import java.util.ArrayList;
import java.util.List;

public class NotificationAdapter
        extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {

    private final List<NotificationResponse.Notification> notifications =
            new ArrayList<>();

    public void setNotifications(List<NotificationResponse.Notification> items) {
        notifications.clear();

        if (items != null) {
            notifications.addAll(items);
        }

        notifyDataSetChanged();
    }

    public void addNotifications(List<NotificationResponse.Notification> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int startPosition = notifications.size();
        notifications.addAll(items);
        notifyItemRangeInserted(startPosition, items.size());
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemNotificationBinding binding = ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new NotificationViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull NotificationViewHolder holder,
            int position
    ) {
        holder.bind(notifications.get(position));
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class NotificationViewHolder extends RecyclerView.ViewHolder {

        private final ItemNotificationBinding binding;

        NotificationViewHolder(ItemNotificationBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(NotificationResponse.Notification notification) {
            binding.txtComments.setText(
                    safeText(notification.getComments(), "No notification message")
            );
            binding.txtType.setText(safeText(notification.getType(), "Notification"));
            binding.txtCreatedAt.setText(
                    safeText(notification.getCreatedAt(), "Time not available")
            );
        }

        private String safeText(String value, String fallback) {
            return value == null || value.trim().isEmpty() ? fallback : value;
        }
    }
}

package com.akramhossain.bimtcharity.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.R;
import com.akramhossain.bimtcharity.databinding.ItemUserBinding;
import com.akramhossain.bimtcharity.models.UserResponse;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private final List<UserResponse.User> users = new ArrayList<>();

    public void setUsers(List<UserResponse.User> items) {
        users.clear();

        if (items != null) {
            users.addAll(items);
        }

        notifyDataSetChanged();
    }

    public void addUsers(List<UserResponse.User> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int startPosition = users.size();
        users.addAll(items);
        notifyItemRangeInserted(startPosition, items.size());
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemUserBinding binding = ItemUserBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new UserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {

        private final ItemUserBinding binding;

        UserViewHolder(ItemUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(UserResponse.User user) {
            binding.txtFullname.setText(
                    safeText(user.getFullname(), "Unnamed member")
            );

            binding.txtMemberCode.setText(
                    safeText(user.getMemberCode(), "No member code")
            );

            binding.txtEmail.setText(
                    safeText(user.getEmail(), "Not set")
            );

            binding.txtPhone.setText(
                    safeText(user.getPhone(), "Not set")
            );

            binding.txtAddress.setText(
                    safeText(user.getAddress(), "Not set")
            );

            binding.txtAcademicInfo.setText(
                    safeText(user.getDepartment(), "Not set")
                            + "  •  Batch "
                            + safeText(user.getBatch(), "Not set")
            );

            binding.txtInvitedBy.setText(
                    safeText(user.getInvitedUser(), "Not set")
            );

            NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);

            binding.txtRecurringAmount.setText(
                    safeText(user.getCurrencyCode(), "")
                            + " "
                            + formatter.format(user.getRecurringAmount())
            );

            if (user.getIsException() == 1) {
                setSpecialMemberStatus();
            } else if (user.getIsActiveDonor() == 1) {
                setActiveDonorStatus();
            } else {
                setInactiveDonorStatus();
            }
        }

        private void setActiveDonorStatus() {
            binding.txtMemberStatus.setText("ACTIVE DONOR");
            binding.txtMemberStatus.setTextColor(
                    Color.parseColor("#198754")
            );
            binding.txtMemberStatus.setBackgroundResource(
                    R.drawable.bg_status_active_donor
            );
        }

        private void setSpecialMemberStatus() {
            binding.txtMemberStatus.setText("SPECIAL MEMBER");
            binding.txtMemberStatus.setTextColor(
                    Color.parseColor("#0D6EFD")
            );
            binding.txtMemberStatus.setBackgroundResource(
                    R.drawable.bg_status_special_member
            );
        }

        private void setInactiveDonorStatus() {
            binding.txtMemberStatus.setText("INACTIVE DONOR");
            binding.txtMemberStatus.setTextColor(
                    Color.parseColor("#DC3545")
            );
            binding.txtMemberStatus.setBackgroundResource(
                    R.drawable.bg_status_inactive_donor
            );
        }

        private String safeText(String value, String fallback) {
            return value == null || value.trim().isEmpty() ? fallback : value;
        }
    }
}

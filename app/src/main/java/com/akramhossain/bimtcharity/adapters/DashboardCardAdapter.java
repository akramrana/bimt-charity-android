package com.akramhossain.bimtcharity.adapters;

import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.databinding.ItemDashboardCardBinding;
import com.akramhossain.bimtcharity.models.DashboardCard;

import java.util.List;

public class DashboardCardAdapter
        extends RecyclerView.Adapter<DashboardCardAdapter.CardViewHolder> {

    private final List<DashboardCard> cards;
    private final OnCardClickListener listener;

    public interface OnCardClickListener {
        void onCardClick(DashboardCard card);
    }

    public DashboardCardAdapter(
            List<DashboardCard> cards,
            OnCardClickListener listener
    ) {
        this.cards = cards;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemDashboardCardBinding binding =
                ItemDashboardCardBinding.inflate(
                        LayoutInflater.from(parent.getContext()),
                        parent,
                        false
                );

        return new CardViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull CardViewHolder holder,
            int position
    ) {
        holder.bind(cards.get(position));
    }

    @Override
    public int getItemCount() {
        return cards.size();
    }

    class CardViewHolder extends RecyclerView.ViewHolder {

        private final ItemDashboardCardBinding binding;

        CardViewHolder(ItemDashboardCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DashboardCard card) {
            binding.txtTitle.setText(card.getTitle());
            binding.txtValue.setText(card.getValue());
            binding.imgIcon.setImageResource(card.getIcon());

            GradientDrawable background = new GradientDrawable();
            background.setColor(card.getColor());
            binding.iconContainer.setBackground(background);

            binding.getRoot().setOnClickListener(view ->
                    listener.onCardClick(card)
            );
        }
    }

    public void updateCards(List<DashboardCard> newCards) {
        cards.clear();
        cards.addAll(newCards);
        notifyDataSetChanged();
    }
}
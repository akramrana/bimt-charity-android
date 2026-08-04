package com.akramhossain.bimtcharity.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.akramhossain.bimtcharity.R;
import com.akramhossain.bimtcharity.adapters.DashboardCardAdapter;
import com.akramhossain.bimtcharity.databinding.FragmentDashboardBinding;
import com.akramhossain.bimtcharity.models.DashboardCard;

import java.util.ArrayList;
import java.util.List;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);

        setupDashboardCards();

        return binding.getRoot();
    }

    private void setupDashboardCards() {
        List<DashboardCard> cards = new ArrayList<>();

        cards.add(new DashboardCard(
                R.drawable.ic_members,
                "NUMBER OF MEMBERS",
                "167",
                Color.parseColor("#F39C12")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_invoice,
                "INVOICE SENT THIS MONTH",
                "0",
                Color.parseColor("#00A65A")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_payment_received,
                "PAYMENT RECEIVED THIS MONTH",
                "0",
                Color.parseColor("#00C0EF")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_fund_request,
                "FUND REQUEST THIS MONTH",
                "0",
                Color.parseColor("#DD4B39")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_payment_release,
                "PAYMENT RELEASE THIS MONTH",
                "0",
                Color.parseColor("#00A65A")
        ));

        cards.add(new DashboardCard(
                R.drawable.ic_expense,
                "EXPENSE THIS MONTH",
                "0",
                Color.parseColor("#00C0EF")
        ));

        DashboardCardAdapter adapter = new DashboardCardAdapter(
                cards,
                card -> Toast.makeText(
                        requireContext(),
                        card.getTitle(),
                        Toast.LENGTH_SHORT
                ).show()
        );

        binding.dashboardRecyclerView.setLayoutManager(
                new LinearLayoutManager(requireContext())
        );

        binding.dashboardRecyclerView.setHasFixedSize(false);
        binding.dashboardRecyclerView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        requireActivity().setTitle("Dashboard");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
package com.akramhossain.bimtcharity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.akramhossain.bimtcharity.databinding.ActivityMainBinding;
import com.akramhossain.bimtcharity.databinding.NavHeaderMainBinding;
import com.akramhossain.bimtcharity.fragments.DashboardFragment;
import com.akramhossain.bimtcharity.fragments.DocumentFragment;
import com.akramhossain.bimtcharity.fragments.EditProfileFragment;
import com.akramhossain.bimtcharity.fragments.FundRequestFragment;
import com.akramhossain.bimtcharity.fragments.InvoiceFragment;
import com.akramhossain.bimtcharity.fragments.NotificationFragment;
import com.akramhossain.bimtcharity.fragments.PaymentReceivedFragment;
import com.akramhossain.bimtcharity.fragments.PaymentReleaseFragment;
import com.akramhossain.bimtcharity.fragments.UserFragment;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        drawerToggle = new ActionBarDrawerToggle(
                this,
                binding.drawerLayout,
                binding.toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );

        binding.drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);
        binding.navigationView.setCheckedItem(R.id.nav_dashboard);

        View headerView = binding.navigationView.getHeaderView(0);

        NavHeaderMainBinding headerBinding = NavHeaderMainBinding.bind(headerView);

        String fullname = getSharedPreferences(
                "bimt_session",
                MODE_PRIVATE
        ).getString("fullname", "Member");

        headerBinding.txtUserName.setText(fullname);

        String memberCode = getSharedPreferences(
                "bimt_session",
                MODE_PRIVATE
        ).getString("member_code", "");

        headerBinding.txtStatus.setText(
                memberCode.isEmpty()
                        ? "● Online"
                        : "● Online  •  " + memberCode
        );

        getOnBackPressedDispatcher().addCallback(
                this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                            binding.drawerLayout.closeDrawer(GravityCompat.START);
                        } else {
                            setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                }
        );

        loadDashboard();
    }

    private void loadDashboard() {

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.contentFrame, new DashboardFragment())
                .commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.nav_dashboard) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new DashboardFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Dashboard");
        } else if (itemId == R.id.nav_invoices) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new InvoiceFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Invoices");
        } else if (itemId == R.id.nav_sadaqah) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new PaymentReceivedFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Sadaqah");
        } else if (itemId == R.id.nav_fund_request) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new FundRequestFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Fund Request");
        } else if (itemId == R.id.nav_donation) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new PaymentReleaseFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Donation");
        }
//        else if (itemId == R.id.nav_expenses) {
//            binding.toolbar.setTitle("Expenses");
//        }
        else if (itemId == R.id.nav_members) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new UserFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Members");
        } else if (itemId == R.id.nav_documents) {
            binding.toolbar.setTitle("Documents");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new DocumentFragment()
                    )
                    .commit();
        } else if (itemId == R.id.nav_activity_log) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new NotificationFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Activity Log");
        }else if (itemId == R.id.nav_edit_profile) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new EditProfileFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Update Profile");
        }
        else if (itemId == R.id.nav_logout) {
            logout();
        }

        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        binding.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    private void logout() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to logout?")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Logout", (dialog, which) -> {

                    getSharedPreferences("bimt_session", MODE_PRIVATE)
                            .edit()
                            .clear()
                            .apply();

                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    intent.addFlags(
                            Intent.FLAG_ACTIVITY_NEW_TASK
                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK
                    );

                    startActivity(intent);
                    finish();

                })
                .show();
    }

}
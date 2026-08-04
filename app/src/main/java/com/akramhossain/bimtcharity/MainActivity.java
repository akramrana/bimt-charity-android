package com.akramhossain.bimtcharity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.akramhossain.bimtcharity.databinding.ActivityMainBinding;
import com.akramhossain.bimtcharity.fragments.DashboardFragment;
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
            binding.toolbar.setTitle("Dashboard");
        } else if (itemId == R.id.nav_invoices) {
            binding.toolbar.setTitle("Invoices");
        } else if (itemId == R.id.nav_sadaqah) {
            binding.toolbar.setTitle("Sadaqah");
        } else if (itemId == R.id.nav_fund_request) {
            binding.toolbar.setTitle("Fund Request");
        } else if (itemId == R.id.nav_donation) {
            binding.toolbar.setTitle("Donation");
        } else if (itemId == R.id.nav_expenses) {
            binding.toolbar.setTitle("Expenses");
        } else if (itemId == R.id.nav_members) {
            binding.toolbar.setTitle("Members");
        } else if (itemId == R.id.nav_documents) {
            binding.toolbar.setTitle("Documents");
        } else if (itemId == R.id.nav_activity_log) {
            binding.toolbar.setTitle("Activity Log");
        } else if (itemId == R.id.nav_report) {
            binding.toolbar.setTitle("Report");
        } else if (itemId == R.id.nav_send_mail) {
            binding.toolbar.setTitle("Send Mail");
        }

        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        binding.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

}
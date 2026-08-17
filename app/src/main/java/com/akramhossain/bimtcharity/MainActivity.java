package com.akramhossain.bimtcharity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;

import com.akramhossain.bimtcharity.databinding.ActivityMainBinding;
import com.akramhossain.bimtcharity.databinding.NavHeaderMainBinding;
import com.akramhossain.bimtcharity.fragments.DashboardFragment;
import com.akramhossain.bimtcharity.fragments.CmsFragment;
import com.akramhossain.bimtcharity.fragments.DocumentFragment;
import com.akramhossain.bimtcharity.fragments.EditProfileFragment;
import com.akramhossain.bimtcharity.fragments.FundRequestFragment;
import com.akramhossain.bimtcharity.fragments.InvoiceFragment;
import com.akramhossain.bimtcharity.fragments.NotificationFragment;
import com.akramhossain.bimtcharity.fragments.PaymentReceivedFragment;
import com.akramhossain.bimtcharity.fragments.PaymentReleaseFragment;
import com.akramhossain.bimtcharity.fragments.SubmitPaymentFragment;
import com.akramhossain.bimtcharity.fragments.UserFragment;
import com.akramhossain.bimtcharity.models.DeleteAccountRequest;
import com.akramhossain.bimtcharity.models.DeleteAccountResponse;
import com.akramhossain.bimtcharity.network.ApiClient;
import com.akramhossain.bimtcharity.network.ApiService;
import com.akramhossain.bimtcharity.service.PushTokenManager;
import com.akramhossain.bimtcharity.utils.Utils;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ActivityMainBinding binding;
    private ActionBarDrawerToggle drawerToggle;
    private MaterialToolbar toolbar;
    private DrawerLayout drawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        toolbar = binding.toolbar;
        drawerLayout = binding.drawerLayout;

        //setSupportActionBar(binding.toolbar);

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

        getSupportFragmentManager()
                .addOnBackStackChangedListener(() -> updateToolbarTitle());

        handleNotificationIntent(getIntent());

        requestNotificationPermission();

        if (Utils.isGooglePlayServicesAvailable(this)) {
            Log.d("PushCheck", "Using FCM");
            getCurrentFCMToken();
        }else {
            Log.e("Push", "No supported push service available.");
        }

        setupToolbar();
    }

    private void updateToolbarTitle() {

        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (fragment instanceof PaymentReceivedFragment) {
            binding.toolbar.setTitle("Sadaqah");

        } else if (fragment instanceof SubmitPaymentFragment) {
            binding.toolbar.setTitle("Submit Payment Proof");

        } else if (fragment instanceof DashboardFragment) {
            binding.toolbar.setTitle("Dashboard");

        } else if (fragment instanceof InvoiceFragment) {
            binding.toolbar.setTitle("Invoices");

        } else if (fragment instanceof FundRequestFragment) {
            binding.toolbar.setTitle("Fund Request");

        } else if (fragment instanceof PaymentReleaseFragment) {
            binding.toolbar.setTitle("Donation");

        } else if (fragment instanceof UserFragment) {
            binding.toolbar.setTitle("Members");

        } else if (fragment instanceof DocumentFragment) {
            binding.toolbar.setTitle("Documents");

        } else if (fragment instanceof NotificationFragment) {
            binding.toolbar.setTitle("Activity Log");

        } else if (fragment instanceof EditProfileFragment) {
            binding.toolbar.setTitle("Update Profile");
        }else if (fragment instanceof CmsFragment) {
            Bundle args = fragment.getArguments();
            if (args != null) {
                binding.toolbar.setTitle(
                        args.getString("ARG_TITLE", "")
                );
            }
        }
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
            showSearchToolbar(false);
        } else if (itemId == R.id.nav_invoices) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new InvoiceFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Invoices");
            showSearchToolbar(true);
        } else if (itemId == R.id.nav_sadaqah) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new PaymentReceivedFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Sadaqah");
            showSearchToolbar(true);
        } else if (itemId == R.id.nav_fund_request) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new FundRequestFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Fund Request");
            showSearchToolbar(true);
        } else if (itemId == R.id.nav_donation) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new PaymentReleaseFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Donation");
            showSearchToolbar(true);
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
            showSearchToolbar(true);
        } else if (itemId == R.id.nav_documents) {
            binding.toolbar.setTitle("Documents");
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new DocumentFragment()
                    )
                    .commit();
            showSearchToolbar(true);
        } else if (itemId == R.id.nav_activity_log) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new NotificationFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Activity Log");
            showSearchToolbar(false);
        }else if (itemId == R.id.nav_edit_profile) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            new EditProfileFragment()
                    )
                    .commit();
            binding.toolbar.setTitle("Update Profile");
            showSearchToolbar(false);
        }
        else if (itemId == R.id.nav_terms) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            CmsFragment.newInstance(1, "Terms & Conditions")
                    )
                    .commit();
            binding.toolbar.setTitle("Terms & Conditions");
            showSearchToolbar(false);
        }
        else if (itemId == R.id.nav_delete_profile) {
            showSearchToolbar(false);
            showDeleteAccountConfirmation();
        }
        else if (itemId == R.id.nav_privacy) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(
                            R.id.contentFrame,
                            CmsFragment.newInstance(2, "Privacy Policy")
                    )
                    .commit();
            binding.toolbar.setTitle("Privacy Policy");
            showSearchToolbar(false);
        }
        else if (itemId == R.id.nav_logout) {
            showSearchToolbar(false);
            logout();
        }

        //Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show();
        binding.drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public void setToolbarTitle(String title) {
        binding.toolbar.setTitle(title);
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

    private void showDeleteAccountConfirmation() {

        new MaterialAlertDialogBuilder(this)
                .setTitle("Delete Account")
                .setMessage(
                        "Are you sure you want to delete your account?\n\n" +
                                "Your account will be deactivated and you will be logged out."
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Delete Account", (dialog, which) -> {
                    deleteAccount();
                })
                .show();
    }

    private void deleteAccount() {

        SharedPreferences preferences =
                getSharedPreferences("bimt_session", MODE_PRIVATE);

        String userId = preferences.getString("member_id", null);

        if (userId == null || userId.trim().isEmpty()) {
            Toast.makeText(
                    this,
                    "Unable to find user information.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        DeleteAccountRequest request = new DeleteAccountRequest(userId);

        ApiService apiService = ApiClient.getApiService();

        apiService.deleteAccount(request).enqueue(new Callback<DeleteAccountResponse>() {
            @Override
            public void onResponse(
                    Call<DeleteAccountResponse> call,
                    Response<DeleteAccountResponse> response
            ) {

                if (response.isSuccessful()
                        && response.body() != null
                        && response.body().isSuccess()) {

                    Toast.makeText(
                            MainActivity.this,
                            "Your account has been deleted.",
                            Toast.LENGTH_SHORT
                    ).show();

                    logoutAfterAccountDeletion();

                } else {

                    String message = "Unable to delete account.";

                    if (response.body() != null
                            && response.body().getMessage() != null
                            && !response.body().getMessage().isEmpty()) {

                        message = response.body().getMessage();
                    }

                    Toast.makeText(
                            MainActivity.this,
                            message,
                            Toast.LENGTH_LONG
                    ).show();
                }
            }

            @Override
            public void onFailure(
                    Call<DeleteAccountResponse> call,
                    Throwable throwable
            ) {

                Toast.makeText(
                        MainActivity.this,
                        "Network error. Please try again.",
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }

    private void logoutAfterAccountDeletion() {

        getSharedPreferences("bimt_session", MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        Intent intent = new Intent(this, LoginActivity.class);

        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );

        startActivity(intent);
        finish();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);

        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {

        if (intent == null) {
            return;
        }

        String screen = intent.getStringExtra("screen");

        if (screen == null || screen.trim().isEmpty()) {
            return;
        }

        switch (screen) {

            case "dashboard":

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.contentFrame,
                                new DashboardFragment()
                        )
                        .commit();

                binding.toolbar.setTitle("Dashboard");

                showSearchToolbar(false);

                break;

            case "sadaqah":

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.contentFrame,
                                new PaymentReceivedFragment()
                        )
                        .commit();

                binding.toolbar.setTitle("Sadaqah");

                showSearchToolbar(true);

                break;

            case "invoice":

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.contentFrame,
                                new InvoiceFragment()
                        )
                        .commit();

                binding.toolbar.setTitle("Invoices");

                showSearchToolbar(true);

                break;

            case "fund_request":

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.contentFrame,
                                new FundRequestFragment()
                        )
                        .commit();

                binding.toolbar.setTitle("Fund Request");

                showSearchToolbar(true);

                break;

            case "donation":

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.contentFrame,
                                new PaymentReleaseFragment()
                        )
                        .commit();

                binding.toolbar.setTitle("Donation");

                showSearchToolbar(true);

                break;

            case "activity_log":

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.contentFrame,
                                new NotificationFragment()
                        )
                        .commit();

                binding.toolbar.setTitle("Activity Log");

                showSearchToolbar(false);

                break;

            case "member":

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.contentFrame,
                                new UserFragment()
                        )
                        .commit();

                binding.toolbar.setTitle("Members");

                showSearchToolbar(true);

                break;

            case "document":

                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(
                                R.id.contentFrame,
                                new DocumentFragment()
                        )
                        .commit();

                binding.toolbar.setTitle("Documents");

                showSearchToolbar(true);

                break;
        }

        // Prevent accidental re-processing later
        intent.removeExtra("screen");
    }

    private final ActivityResultLauncher<String>
            notificationPermissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    isGranted -> {
                        if (isGranted) {
                            Log.d("FirebasePush", "Notification permission granted");
                        } else {
                            Log.d("FirebasePush", "Notification permission denied");
                        }
                    }
            );

    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED) {

                notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                );
            }
        }
    }

    private void getCurrentFCMToken(){
        FirebaseMessaging.getInstance()
                .getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e(
                                "FirebasePush",
                                "Fetching FCM token failed",
                                task.getException()
                        );
                        return;
                    }

                    String token = task.getResult();

                    Log.d("FirebasePush", "FCM token: " + token);

                    FirebaseMessaging.getInstance()
                            .subscribeToTopic("all")
                            .addOnCompleteListener(topicTask -> {
                                if (topicTask.isSuccessful()) {
                                    Log.d("FirebasePush", "Subscribed to topic: all");
                                } else {
                                    Log.e(
                                            "FirebasePush",
                                            "Topic subscription failed",
                                            topicTask.getException()
                                    );
                                }
                            });

                    PushTokenManager.sendTokenToServer(
                            getApplicationContext(),
                            token,
                            "fcm"
                    );
                });
    }

    private void setupToolbar() {

        toolbar.setNavigationOnClickListener(v -> {
            drawerLayout.openDrawer(GravityCompat.START);
        });

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_search) {
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.contentFrame);
                if (fragment instanceof InvoiceFragment) {
                    ((InvoiceFragment) fragment).showSearch();
                }
                if (fragment instanceof PaymentReceivedFragment) {
                    ((PaymentReceivedFragment) fragment).showSearch();
                }
                if (fragment instanceof FundRequestFragment) {
                    ((FundRequestFragment) fragment).showSearch();
                }
                if (fragment instanceof PaymentReleaseFragment) {
                    ((PaymentReleaseFragment) fragment).showSearch();
                }
                return true;
            }
            return false;
        });
    }

    private void showSearchToolbar(boolean showSearch) {
        MenuItem searchItem = toolbar.getMenu().findItem(R.id.action_search);
        if (searchItem != null) {
            searchItem.setVisible(showSearch);
        }
        drawerLayout.closeDrawers();
    }

}



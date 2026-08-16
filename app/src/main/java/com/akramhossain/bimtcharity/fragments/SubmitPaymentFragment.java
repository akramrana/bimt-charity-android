package com.akramhossain.bimtcharity.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akramhossain.bimtcharity.R;
import com.akramhossain.bimtcharity.databinding.FragmentSubmitPaymentBinding;
import com.akramhossain.bimtcharity.models.PaymentSubmitRequest;
import com.akramhossain.bimtcharity.models.PaymentSubmitResponse;
import com.akramhossain.bimtcharity.models.UnpaidInvoice;
import com.akramhossain.bimtcharity.models.UnpaidInvoiceResponse;
import com.akramhossain.bimtcharity.network.ApiClient;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubmitPaymentFragment extends Fragment {
    private static final String SESSION_NAME = "bimt_session";
    private static final int BDT_CURRENCY_ID = 13;
    private static final int MAX_IMAGE_DIMENSION = 1600;
    private static final String[] CURRENCY_CODES = (
            "AED,AFN,ALL,AMD,ANG,AOA,ARS,AUD,AWG,AZN,BAM,BBD,BDT,BGN,BD,BIF,"
                    + "BMD,BND,BOB,BRL,BSD,BTC,BTN,BWP,BYN,BYR,BZD,CAD,CDF,CHF,CLF,CLP,"
                    + "CNH,CNY,COP,CRC,CUC,CUP,CVE,CZK,DJF,DKK,DOP,DZD,EGP,ERN,ETB,EUR,"
                    + "FJD,FKP,GBP,GEL,GGP,GHS,GIP,GMD,GNF,GTQ,GYD,HKD,HNL,HRK,HTG,HUF,"
                    + "IDR,ILS,IMP,INR,IQD,IRR,ISK,JEP,JMD,JOD,JPY,KES,KGS,KHR,KMF,KPW,"
                    + "KRW,KD,KYD,KZT,LAK,LBP,LKR,LRD,LSL,LYD,MAD,MDL,MGA,MKD,MMK,MNT,"
                    + "MOP,MRO,MUR,MVR,MWK,MXN,MYR,MZN,NAD,NGN,NIO,NOK,NPR,NZD,OR,PAB,"
                    + "PEN,PGK,PHP,PKR,PLN,PYG,QR,RON,RSD,RUB,RWF,SR,SBD,SCR,SDG,SEK,SGD,"
                    + "SHP,SLL,SOS,SRD,SSP,STD,SVC,SYP,SZL,THB,TJS,TMT,TND,TOP,TRY,TTD,"
                    + "TWD,TZS,UAH,UGX,USD,UYU,UZS,VEF,VND,VUV,WST,XAF,XAG,XAU,XCD,XDR,"
                    + "XOF,XPD,XPF,XPT,YER,ZAR,ZMK,ZMW,ZWL"
    ).split(",");

    private final List<UnpaidInvoice> unpaidInvoices = new ArrayList<>();
    private final ExecutorService imageExecutor = Executors.newSingleThreadExecutor();
    private FragmentSubmitPaymentBinding binding;
    private UnpaidInvoice selectedInvoice;
    private String selectedImageBase64;
    private int selectedCurrencyId = BDT_CURRENCY_ID;
    private boolean submitting;
    private boolean loadingInvoices;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    this::handleSelectedImage);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSubmitPaymentBinding.inflate(inflater, container, false);
        setupDropdowns();
        setupListeners();
        setDefaultDate();
        showMode(true);
        loadUnpaidInvoices();
        return binding.getRoot();
    }

    private void setupDropdowns() {
        String[] months = getResources().getStringArray(R.array.payment_months);
        binding.monthDropdown.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, months));

        Calendar calendar = Calendar.getInstance();
        binding.monthDropdown.setText(months[calendar.get(Calendar.MONTH)], false);
        List<String> years = new ArrayList<>();
        int currentYear = calendar.get(Calendar.YEAR);
        for (int year = currentYear - 5; year <= currentYear; year++) {
            years.add(String.valueOf(year));
        }
        binding.yearDropdown.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, years));
        binding.yearDropdown.setText(String.valueOf(currentYear), false);

        binding.currencyDropdown.setAdapter(new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_dropdown_item_1line, CURRENCY_CODES));
        binding.currencyDropdown.setText("BDT", false);
        binding.currencyDropdown.setOnItemClickListener((parent, view, position, id) -> {
            // Backend currency IDs in the supplied list are sequential and one-based.
            selectedCurrencyId = position + 1;
            binding.currencyLayout.setError(null);
        });
    }

    private void setupListeners() {
        binding.paymentModeGroup.addOnButtonCheckedListener((group, checkedId, checked) -> {
            if (checked) {
                showMode(checkedId == R.id.btnAgainstInvoice);
            }
        });
        binding.invoiceDropdown.setOnItemClickListener((parent, view, position, id) -> {
            selectedInvoice = unpaidInvoices.get(position);
            binding.invoiceLayout.setError(null);
        });
        binding.dateEditText.setOnClickListener(view -> showDatePicker());
        binding.dateLayout.setEndIconOnClickListener(view -> showDatePicker());
        binding.chooseProofButton.setOnClickListener(view -> imagePicker.launch("image/*"));
        binding.submitButton.setOnClickListener(view -> validateAndSubmit());
    }

    private void showMode(boolean againstInvoice) {
        binding.invoiceLayout.setVisibility(againstInvoice ? View.VISIBLE : View.GONE);
        binding.txtInvoiceEmpty.setVisibility(againstInvoice && unpaidInvoices.isEmpty()
                && !loadingInvoices ? View.VISIBLE : View.GONE);
        binding.withoutInvoiceFields.setVisibility(againstInvoice ? View.GONE : View.VISIBLE);
        clearErrors();
    }

    private void setDefaultDate() {
        binding.dateEditText.setText(new SimpleDateFormat("yyyy-MM-dd", Locale.US)
                .format(Calendar.getInstance().getTime()));
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        new DatePickerDialog(requireContext(), (picker, year, month, day) ->
                binding.dateEditText.setText(String.format(Locale.US,
                        "%04d-%02d-%02d", year, month + 1, day)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void handleSelectedImage(Uri uri) {
        if (uri == null || binding == null) {
            return;
        }
        binding.chooseProofButton.setEnabled(false);
        binding.chooseProofButton.setText("Processing image...");
        imageExecutor.execute(() -> {
            try {
                String encoded = resizeAndEncode(uri);
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    selectedImageBase64 = encoded;
                    binding.proofPreview.setPadding(0, 0, 0, 0);
                    binding.proofPreview.setImageURI(uri);
                    binding.proofError.setVisibility(View.GONE);
                    binding.chooseProofButton.setEnabled(!submitting);
                    binding.chooseProofButton.setText("Change payment proof image");
                });
            } catch (IOException | RuntimeException exception) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    binding.chooseProofButton.setEnabled(!submitting);
                    binding.chooseProofButton.setText("Choose payment proof image");
                    Toast.makeText(requireContext(), "Unable to read selected image",
                            Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private String resizeAndEncode(Uri uri) throws IOException {
        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        try (InputStream input = requireContext().getContentResolver().openInputStream(uri)) {
            if (input == null) throw new IOException("Image unavailable");
            BitmapFactory.decodeStream(input, null, bounds);
        }
        int sample = 1;
        while (bounds.outWidth / sample > MAX_IMAGE_DIMENSION
                || bounds.outHeight / sample > MAX_IMAGE_DIMENSION) {
            sample *= 2;
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sample;
        Bitmap bitmap;
        try (InputStream input = requireContext().getContentResolver().openInputStream(uri)) {
            if (input == null) throw new IOException("Image unavailable");
            bitmap = BitmapFactory.decodeStream(input, null, options);
        }
        if (bitmap == null) throw new IOException("Unsupported image");
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 82, output);
        bitmap.recycle();
        return Base64.encodeToString(output.toByteArray(), Base64.NO_WRAP);
    }

    private void loadUnpaidInvoices() {
        String userId = getUserIdString();
        if (TextUtils.isEmpty(userId)) {
            binding.txtInvoiceEmpty.setText("Member information not found.");
            binding.txtInvoiceEmpty.setVisibility(View.VISIBLE);
            return;
        }
        loadingInvoices = true;
        binding.txtInvoiceEmpty.setVisibility(View.GONE);
        updateLoadingState();
        ApiClient.getApiService().getUnpaidInvoices(userId)
                .enqueue(new Callback<UnpaidInvoiceResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<UnpaidInvoiceResponse> call,
                                           @NonNull Response<UnpaidInvoiceResponse> response) {
                        if (binding == null) return;
                        loadingInvoices = false;
                        unpaidInvoices.clear();
                        UnpaidInvoiceResponse result = response.body();
                        if (response.isSuccessful() && result != null && result.isSuccess()
                                && result.getData() != null
                                && result.getData().getDataProvider() != null) {
                            unpaidInvoices.addAll(result.getData().getDataProvider());
                        } else {
                            Toast.makeText(requireContext(), responseMessage(response,
                                    "Unable to load unpaid invoices"), Toast.LENGTH_LONG).show();
                        }
                        selectedInvoice = null;
                        binding.invoiceDropdown.setText("", false);
                        binding.invoiceDropdown.setAdapter(new ArrayAdapter<>(requireContext(),
                                android.R.layout.simple_dropdown_item_1line, unpaidInvoices));
                        binding.invoiceDropdown.setEnabled(!unpaidInvoices.isEmpty());
                        binding.txtInvoiceEmpty.setText("No unpaid invoices found.");
                        binding.txtInvoiceEmpty.setVisibility(isAgainstInvoice()
                                && unpaidInvoices.isEmpty() ? View.VISIBLE : View.GONE);
                        updateLoadingState();
                    }

                    @Override
                    public void onFailure(@NonNull Call<UnpaidInvoiceResponse> call,
                                          @NonNull Throwable throwable) {
                        if (binding == null) return;
                        loadingInvoices = false;
                        binding.txtInvoiceEmpty.setText("Unable to load unpaid invoices.");
                        binding.txtInvoiceEmpty.setVisibility(isAgainstInvoice()
                                ? View.VISIBLE : View.GONE);
                        updateLoadingState();
                        Toast.makeText(requireContext(), "Connection failed: "
                                + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void validateAndSubmit() {
        if (submitting) return;
        clearErrors();
        boolean valid = true;
        String date = textOf(binding.dateEditText);
        if (date.isEmpty()) {
            binding.dateLayout.setError("Received date is required");
            valid = false;
        }
        if (selectedImageBase64 == null) {
            binding.proofError.setVisibility(View.VISIBLE);
            valid = false;
        }
        boolean againstInvoice = isAgainstInvoice();
        double amount = 0;
        if (againstInvoice && selectedInvoice == null) {
            binding.invoiceLayout.setError("Select an unpaid invoice");
            valid = false;
        } else if (!againstInvoice) {
            try {
                amount = Double.parseDouble(textOf(binding.amountEditText));
                if (amount <= 0) throw new NumberFormatException();
            } catch (NumberFormatException exception) {
                binding.amountLayout.setError("Enter an amount greater than 0");
                valid = false;
            }
            if (textOf(binding.currencyDropdown).isEmpty() || selectedCurrencyId <= 0) {
                binding.currencyLayout.setError("Currency is required");
                valid = false;
            }
            if (textOf(binding.monthDropdown).isEmpty()) {
                binding.monthLayout.setError("Instalment month is required");
                valid = false;
            }
            if (textOf(binding.yearDropdown).isEmpty()) {
                binding.yearLayout.setError("Instalment year is required");
                valid = false;
            }
        }
        int userId = getUserId();
        if (userId <= 0) {
            Toast.makeText(requireContext(), "Member information not found",
                    Toast.LENGTH_LONG).show();
            valid = false;
        }
        if (!valid) return;

        String comments = textOf(binding.commentsEditText);
        PaymentSubmitRequest request = againstInvoice
                ? PaymentSubmitRequest.againstInvoice(userId, date, comments,
                selectedInvoice.getMonthlyInvoiceId(), selectedImageBase64)
                : PaymentSubmitRequest.withoutInvoice(userId, date, comments, amount,
                selectedCurrencyId, textOf(binding.monthDropdown),
                textOf(binding.yearDropdown), selectedImageBase64);
        submit(request);
    }

    private void submit(PaymentSubmitRequest request) {
        submitting = true;
        updateLoadingState();
        ApiClient.getApiService().submitPayment(request)
                .enqueue(new Callback<PaymentSubmitResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PaymentSubmitResponse> call,
                                           @NonNull Response<PaymentSubmitResponse> response) {
                        if (binding == null) return;
                        submitting = false;
                        updateLoadingState();
                        PaymentSubmitResponse result = response.body();
                        if (!response.isSuccessful() || result == null || !result.isSuccess()) {
                            Toast.makeText(requireContext(), responseMessage(response,
                                    result == null ? "Unable to submit payment" : result.getMessage()),
                                    Toast.LENGTH_LONG).show();
                            return;
                        }
                        String message = TextUtils.isEmpty(result.getMessage())
                                ? "Payment submitted successfully" : result.getMessage();
                        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
                        resetForm();
                        loadUnpaidInvoices();
                    }

                    @Override
                    public void onFailure(@NonNull Call<PaymentSubmitResponse> call,
                                          @NonNull Throwable throwable) {
                        if (binding == null) return;
                        submitting = false;
                        updateLoadingState();
                        Toast.makeText(requireContext(), "Connection failed: "
                                + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void resetForm() {
        selectedInvoice = null;
        selectedImageBase64 = null;
        binding.invoiceDropdown.setText("", false);
        binding.amountEditText.setText("");
        selectedCurrencyId = BDT_CURRENCY_ID;
        binding.currencyDropdown.setText("BDT", false);
        binding.commentsEditText.setText("");
        binding.proofPreview.setImageResource(R.drawable.ic_upload);
        int previewPadding = Math.round(36 * getResources().getDisplayMetrics().density);
        binding.proofPreview.setPadding(previewPadding, previewPadding,
                previewPadding, previewPadding);
        binding.chooseProofButton.setText("Choose payment proof image");
        setDefaultDate();
    }

    private void updateLoadingState() {
        if (binding == null) return;
        binding.progressIndicator.setVisibility(submitting || loadingInvoices
                ? View.VISIBLE : View.GONE);
        binding.submitButton.setEnabled(!submitting);
        binding.chooseProofButton.setEnabled(!submitting);
        binding.submitButton.setText(submitting ? "Submitting..." : "Submit Payment");
    }

    private boolean isAgainstInvoice() {
        return binding.paymentModeGroup.getCheckedButtonId() == R.id.btnAgainstInvoice;
    }

    private void clearErrors() {
        binding.invoiceLayout.setError(null);
        binding.amountLayout.setError(null);
        binding.currencyLayout.setError(null);
        binding.monthLayout.setError(null);
        binding.yearLayout.setError(null);
        binding.dateLayout.setError(null);
        binding.proofError.setVisibility(View.GONE);
    }

    private String textOf(android.widget.EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString().trim();
    }

    private String getUserIdString() {
        return requireContext().getSharedPreferences(SESSION_NAME, Context.MODE_PRIVATE)
                .getString("member_id", "");
    }

    private int getUserId() {
        try {
            return Integer.parseInt(getUserIdString());
        } catch (NumberFormatException exception) {
            return 0;
        }
    }

    private String responseMessage(Response<?> response, String fallback) {
        if (response.body() instanceof PaymentSubmitResponse) {
            String message = ((PaymentSubmitResponse) response.body()).getMessage();
            if (!TextUtils.isEmpty(message)) return message;
        }
        ResponseBody errorBody = response.errorBody();
        if (errorBody != null) {
            try {
                JsonObject json = JsonParser.parseString(errorBody.string()).getAsJsonObject();
                if (json.has("message") && !json.get("message").isJsonNull()) {
                    String message = json.get("message").getAsString();
                    if (!TextUtils.isEmpty(message)) return message;
                }
            } catch (Exception ignored) {
                // Fall back to the user-friendly message below.
            }
        }
        return TextUtils.isEmpty(fallback) ? "Request failed" : fallback;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        imageExecutor.shutdownNow();
    }
}

package com.akramhossain.bimtcharity.adapters;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.akramhossain.bimtcharity.databinding.ItemDocumentBinding;
import com.akramhossain.bimtcharity.models.DocumentResponse;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter
        extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {

    private static final String DOCUMENT_BASE_URL =
            "http://bimtcharity.org/uploads/";

    private final List<DocumentResponse.Document> documents = new ArrayList<>();

    public void setDocuments(List<DocumentResponse.Document> items) {
        documents.clear();

        if (items != null) {
            documents.addAll(items);
        }

        notifyDataSetChanged();
    }

    public void addDocuments(List<DocumentResponse.Document> items) {
        if (items == null || items.isEmpty()) {
            return;
        }

        int startPosition = documents.size();
        documents.addAll(items);
        notifyItemRangeInserted(startPosition, items.size());
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {
        ItemDocumentBinding binding = ItemDocumentBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );

        return new DocumentViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(
            @NonNull DocumentViewHolder holder,
            int position
    ) {
        holder.bind(documents.get(position));
    }

    @Override
    public int getItemCount() {
        return documents.size();
    }

    static class DocumentViewHolder extends RecyclerView.ViewHolder {

        private final ItemDocumentBinding binding;

        DocumentViewHolder(ItemDocumentBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DocumentResponse.Document document) {
            binding.txtTitle.setText(safeText(document.getTitle(), "Untitled document"));
            binding.txtDescription.setText(
                    safeText(document.getDescription(), "No description")
            );
            binding.txtUploadedBy.setText(
                    "Uploaded by: " + safeText(document.getUser(), "Not set")
            );
            binding.txtCreatedAt.setText(
                    "Uploaded: " + safeText(document.getCreatedAt(), "Not set")
            );
            String file = document.getFile();
            boolean hasFile = file != null && !file.trim().isEmpty();

            binding.txtFile.setText(hasFile ? "Download File" : "File unavailable");
            binding.txtFile.setEnabled(hasFile);

            if (hasFile) {
                /*binding.txtFile.setOnClickListener(view -> {
                    Uri fileUri = Uri.parse(
                            DOCUMENT_BASE_URL + Uri.encode(file.trim())
                    );
                    Intent intent = new Intent(Intent.ACTION_VIEW, fileUri);
                    view.getContext().startActivity(intent);
                });*/

                binding.layoutDownload.setOnClickListener(view -> {

                    String fileUrl = DOCUMENT_BASE_URL + Uri.encode(file.trim());

                    Uri uri = Uri.parse(fileUrl);

                    DownloadManager.Request request = new DownloadManager.Request(uri);

                    request.setTitle("Downloading document");
                    request.setDescription(document.getTitle());

                    request.setNotificationVisibility(
                            DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                    );

                    request.setDestinationInExternalPublicDir(
                            Environment.DIRECTORY_DOWNLOADS,
                            file.trim()
                    );

                    request.setAllowedOverMetered(true);
                    request.setAllowedOverRoaming(true);

                    DownloadManager downloadManager =
                            (DownloadManager) view.getContext()
                                    .getSystemService(Context.DOWNLOAD_SERVICE);

                    if (downloadManager != null) {
                        downloadManager.enqueue(request);

                        Toast.makeText(
                                view.getContext(),
                                "Download started",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
            } else {
                binding.txtFile.setOnClickListener(null);
            }
        }

        private String safeText(String value, String fallback) {
            return value == null || value.trim().isEmpty() ? fallback : value;
        }
    }
}
